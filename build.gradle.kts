import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import com.jetbrains.plugin.structure.base.utils.isFile
import groovy.ant.FileNameFinder
import org.apache.tools.ant.taskdefs.condition.Os
import org.gradle.kotlin.dsl.register
import org.jetbrains.grammarkit.tasks.GenerateLexerTask
import org.jetbrains.grammarkit.tasks.GenerateParserTask
import org.jetbrains.intellij.platform.gradle.Constants
import org.gradle.process.ExecOperations
import java.io.ByteArrayOutputStream
import javax.inject.Inject

// Interface for injecting ExecOperations service (Gradle 9/10 compatibility)
interface InjectedExecOps {
    @get:Inject val execOps: ExecOperations
}

plugins {
    id("java")
    alias(libs.plugins.kotlinJvm)
    id("org.jetbrains.intellij.platform") version "2.10.4"     // See https://github.com/JetBrains/intellij-platform-gradle-plugin/releases
    id("org.jetbrains.grammarkit") version "2023.3.0.1"
}

val isWindows = Os.isFamily(Os.FAMILY_WINDOWS)
extra["isWindows"] = isWindows

val dotnetSolution: String by project
val buildConfiguration: String by project
val productVersion: String by project
val dotnetPluginId: String by project
val riderPluginId: String by project
val publishToken: String by project

allprojects {
    repositories {
        maven { setUrl("https://cache-redirector.jetbrains.com/maven-central") }
    }
}

repositories {
    intellijPlatform {
        defaultRepositories()
        jetbrainsRuntime()
    }
}

tasks.wrapper {
    gradleVersion = "8.8"
    distributionType = Wrapper.DistributionType.ALL
    distributionUrl = "https://cache-redirector.jetbrains.com/services.gradle.org/distributions/gradle-${gradleVersion}-all.zip"
}

version = extra["pluginVersion"] as String

tasks.processResources {
    from("dependencies.json") { into("META-INF") }
}

sourceSets {
    main {
        java.srcDir("src/rider/main/java")
        kotlin.srcDir("src/rider/main/kotlin")
        resources.srcDir("src/rider/main/resources")
        // Include generated parser/lexer code for basic syntax highlighting
        java.srcDir("src/main/gen")
    }
    test {
        kotlin.srcDir("src/test/kotlin")
        resources.srcDir("src/test/resources")
    }
}

grammarKit {
    tasks.register<GenerateLexerTask>("generateAngelScriptLexer") {
        sourceFile.set(file("src/main/jflex/AngelScript.flex"))
        targetOutputDir.set(file("src/main/gen/com/scriptacus/riderunrealangelscript/lang/lexer"))
    }
    tasks.register<GenerateParserTask>("generateAngelScriptParser") {
        dependsOn("generateAngelScriptLexer")
        sourceFile.set(file("src/main/bnf/AngelScript.bnf"))
        targetRootOutputDir.set(file("src/main/gen"))
        pathToParser.set("com/scriptacus/riderunrealangelscript/lang/parser")
        pathToPsiRoot.set("com/scriptacus/riderunrealangelscript/lang/psi")
    }
}

tasks.withType<KotlinCompile>().configureEach {
    dependsOn("generateAngelScriptParser")
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

val setBuildTool by tasks.registering {
    // Capture project properties at configuration time
    val solution = dotnetSolution
    val config = buildConfiguration
    val rootPath = rootDir
    val isWin = isWindows

    // Get injected ExecOperations at configuration time
    val execOps = project.objects.newInstance<InjectedExecOps>().execOps

    doLast {
        extra["executable"] = "dotnet"
        var args = mutableListOf("msbuild")

        if (isWin) {
            val stdout = ByteArrayOutputStream()
            execOps.exec {
                executable("${rootPath}\\tools\\vswhere.exe")
                args("-latest", "-property", "installationPath", "-products", "*")
                standardOutput = stdout
                workingDir(rootPath)
            }

            val directory = stdout.toString().trim()
            if (directory.isNotEmpty()) {
                val files = FileNameFinder().getFileNames("${directory}\\MSBuild", "**/MSBuild.exe")
                extra["executable"] = files[0]
                args = mutableListOf("/v:minimal")
            }
        }

        args.add(solution)
        args.add("/p:Configuration=${config}")
        args.add("/p:HostFullIdentifier=")
        extra["args"] = args
    }
}

val compileDotNet by tasks.registering {
    dependsOn(setBuildTool)

    // Capture project properties at configuration time
    val rootPath = rootDir

    // Get injected ExecOperations at configuration time
    val execOps = project.objects.newInstance<InjectedExecOps>().execOps

    doLast {
        val executable: String by setBuildTool.get().extra
        val arguments = (setBuildTool.get().extra["args"] as List<*>).toMutableList()
        arguments.add("/t:Restore;Rebuild")
        execOps.exec {
            executable(executable)
            args(arguments)
            workingDir(rootPath)
        }
    }
}

val testDotNet by tasks.registering {
    // Capture project properties at configuration time
    val solution = dotnetSolution
    val rootPath = rootDir

    // Get injected ExecOperations at configuration time
    val execOps = project.objects.newInstance<InjectedExecOps>().execOps

    doLast {
        execOps.exec {
            executable("dotnet")
            args("test", solution,"--logger","GitHubActions")
            workingDir(rootPath)
        }
    }
}

tasks.buildPlugin {
    // Capture project properties at configuration time
    val rootPath = rootDir
    val projectVersion = version
    val projectName = rootProject.name

    // Get injected ExecOperations at configuration time
    val execOps = project.objects.newInstance<InjectedExecOps>().execOps

    doLast {
        val zipFile = file("${layout.buildDirectory.get()}/distributions/${projectName}-${projectVersion}.zip")
        val outputDir = file("${rootPath}/output")

        println("Looking for ZIP file at: ${zipFile.absolutePath}")
        println("ZIP file exists: ${zipFile.exists()}")
        println("Copying to: ${outputDir.absolutePath}")

        if (!outputDir.exists()) {
            outputDir.mkdirs()
        }

        copy {
            from(zipFile)
            into(outputDir)
        }

        val copiedFile = file("${outputDir}/${projectName}-${projectVersion}.zip")
        println("Copied file exists: ${copiedFile.exists()}")
        if (!copiedFile.exists()) {
            throw GradleException("Failed to copy plugin ZIP to output directory")
        }

        // TODO: See also org.jetbrains.changelog: https://github.com/JetBrains/gradle-changelog-plugin
        val changelogText = file("${rootPath}/CHANGELOG.md").readText()
        val changelogMatches = Regex("(?s)(-.+?)(?=##|$)").findAll(changelogText)
        val changeNotes = changelogMatches.map {
            it.groups[1]!!.value.replace("(?s)- ".toRegex(), "\u2022 ").replace("`", "").replace(",", "%2C").replace(";", "%3B")
        }.take(1).joinToString()

        val executable: String by setBuildTool.get().extra
        val arguments = (setBuildTool.get().extra["args"] as List<*>).toMutableList()
        arguments.add("/t:Pack")
        arguments.add("/p:PackageOutputPath=${rootPath}/output")
        arguments.add("/p:PackageReleaseNotes=${changeNotes}")
        arguments.add("/p:PackageVersion=${projectVersion}")
        execOps.exec {
            executable(executable)
            args(arguments)
            workingDir(rootPath)
        }
    }
}

dependencies {
    intellijPlatform {
        rider(productVersion) { this.useInstaller = false }
        jetbrainsRuntime()

        // Bundled plugins
        bundledPlugin("org.jetbrains.plugins.textmate")
        bundledPlugin("JavaScript") // For NodeJS interpreter discovery

        // External plugins
        plugin("com.redhat.devtools.lsp4ij", "0.19.1")

        // Test framework
        testFramework(org.jetbrains.intellij.platform.gradle.TestFrameworkType.Platform)
    }

    // Add junit-vintage for JUnit 4 tests
    testImplementation("org.junit.vintage:junit-vintage-engine:5.10.1")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.runIde {
    // Match Rider's default heap size of 1.5Gb (default for runIde is 512Mb)
    maxHeapSize = "1500m"
}

tasks.test {
    useJUnitPlatform()
}

tasks.processTestResources {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}

tasks.patchPluginXml {
    // TODO: See also org.jetbrains.changelog: https://github.com/JetBrains/gradle-changelog-plugin
    val changelogText = file("${rootDir}/CHANGELOG.md").readText()
    val changelogMatches = Regex("(?s)(-.+?)(?=##|$)").findAll(changelogText)

    changeNotes.set(changelogMatches.map {
        it.groups[1]!!.value.replace("(?s)\r?\n".toRegex(), "<br />\n")
    }.take(1).joinToString())
}

// Install npm dependencies in the third-party VSCode extension
val installThirdPartyDeps by tasks.registering(Exec::class) {
    val thirdPartyDir = file("third-party/vscode-unreal-angelscript")
    workingDir(thirdPartyDir)

    val npmCommand = findProperty("npm.executable") as String?
        ?: System.getenv("NPM_EXECUTABLE")
        ?: findNpmInPath()
        ?: throw GradleException("npm not found in PATH. Please install Node.js or set npm.executable property")

    commandLine(npmCommand, "install")

    inputs.file("$thirdPartyDir/package.json")
    outputs.dir("$thirdPartyDir/node_modules")
}

// Compile the third-party VSCode extension language server (TypeScript → JavaScript)
val compileThirdPartyLsp by tasks.registering(Exec::class) {
    dependsOn(installThirdPartyDeps)
    val thirdPartyDir = file("third-party/vscode-unreal-angelscript")
    workingDir(thirdPartyDir)

    val npmCommand = findProperty("npm.executable") as String?
        ?: System.getenv("NPM_EXECUTABLE")
        ?: findNpmInPath()
        ?: throw GradleException("npm not found in PATH. Please install Node.js or set npm.executable property")

    commandLine(npmCommand, "run", "compile")

    inputs.files("$thirdPartyDir/package.json", "$thirdPartyDir/tsconfig.json")
    inputs.dir("$thirdPartyDir/language-server/src")
    outputs.dir("$thirdPartyDir/language-server/out")
}

// Install bundler dependencies (esbuild, etc.) in project root
val installBundlerDeps by tasks.registering(Exec::class) {
    workingDir(rootDir)

    val npmCommand = findProperty("npm.executable") as String?
        ?: System.getenv("NPM_EXECUTABLE")
        ?: findNpmInPath()
        ?: throw GradleException("npm not found in PATH. Please install Node.js or set npm.executable property")

    commandLine(npmCommand, "install")

    inputs.file("package.json")
    outputs.dir("node_modules")
}

val buildLsp by tasks.registering(Exec::class) {
    dependsOn(compileThirdPartyLsp, installBundlerDeps)
    workingDir(rootDir)

    // Allow override via gradle property or environment variable
    val npmCommand = findProperty("npm.executable") as String?
        ?: System.getenv("NPM_EXECUTABLE")
        ?: findNpmInPath()
        ?: throw GradleException("npm not found in PATH. Please install Node.js or set npm.executable property")

    // This will run both LSP and DAP bundling
    commandLine(npmCommand, "run", "bundle")

    inputs.dir("third-party/vscode-unreal-angelscript/language-server/out")
    inputs.files("scripts/bundle-lsp.js", "scripts/bundle-dap.js")
    outputs.file("src/rider/main/resources/js/angelscript-language-server.js")
    outputs.file("src/rider/main/resources/js/angelscript-debug-adapter.js")
}

// Ensure resources are processed after LSP is bundled
tasks.processResources {
    dependsOn(buildLsp)
}

fun findNpmInPath(): String? {
    val npmExecutable = if (isWindows) "npm.cmd" else "npm"
    val path = System.getenv("PATH") ?: return null

    return path.split(File.pathSeparator)
        .map { File(it, npmExecutable) }
        .firstOrNull { it.exists() && it.canExecute() }
        ?.absolutePath
}

tasks.prepareSandbox {
    dependsOn(compileDotNet)
    dependsOn(buildLsp)

    val outputFolder = "${rootDir}/src/dotnet/${dotnetPluginId}/bin/${dotnetPluginId}.Rider/${buildConfiguration}"
    val dllFiles = listOf(
            "$outputFolder/${dotnetPluginId}.dll",
            "$outputFolder/${dotnetPluginId}.pdb",

            // TODO: add additional assemblies
    )

    dllFiles.forEach { f ->
        val file = file(f)
        from(file) { into("${rootProject.name}/dotnet") }
    }

    doLast {
        dllFiles.forEach { f ->
            val file = file(f)
            if (!file.exists()) throw RuntimeException("File $file does not exist")
        }
    }
}

tasks.publishPlugin {
    dependsOn(testDotNet)
    dependsOn(tasks.buildPlugin)
    token.set(publishToken)

    // Capture project properties at configuration time
    val pluginId = dotnetPluginId
    val projectVersion = version
    val token = publishToken
    val rootPath = rootDir

    // Get injected ExecOperations at configuration time
    val execOps = project.objects.newInstance<InjectedExecOps>().execOps

    doLast {
        execOps.exec {
            executable("dotnet")
            args("nuget","push","output/${pluginId}.${projectVersion}.nupkg","--api-key", token,"--source","https://plugins.jetbrains.com")
            workingDir(rootPath)
        }
    }
}

val riderModel: Configuration by configurations.creating {
    isCanBeConsumed = true
    isCanBeResolved = false
}

artifacts {
    add(riderModel.name, provider {
        intellijPlatform.platformPath.resolve("lib/rd/rider-model.jar").also {
            check(it.isFile) {
                "rider-model.jar is not found at $riderModel"
            }
        }
    }) {
        builtBy(Constants.Tasks.INITIALIZE_INTELLIJ_PLATFORM_PLUGIN)
    }
}

// Skip buildSearchableOptions - we don't provide any settings pages yet
// Re-enable this when we add Configurable settings pages for search discoverability
tasks.buildSearchableOptions {
    enabled = false
}
