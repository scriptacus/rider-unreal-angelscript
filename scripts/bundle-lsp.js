const esbuild = require('esbuild');
const path = require('path');
const fs = require('fs');

// Project root is one level up from scripts/
const projectRoot = path.join(__dirname, '..');

async function bundle() {
  try {
    // First, bundle the server
    await esbuild.build({
      entryPoints: [path.join(projectRoot, 'third-party', 'vscode-unreal-angelscript', 'language-server', 'out', 'server.js')],
      bundle: true,
      outfile: path.join(projectRoot, 'src', 'rider', 'main', 'resources', 'js', 'angelscript-language-server.js'),
      platform: 'node',
      target: 'node14',
      format: 'cjs',
      external: [],
      minify: false,
      sourcemap: false,
      logLevel: 'info',
    });

    // Patch the bundled file to use stdin/stdout instead of IPC
    const bundledPath = path.join(projectRoot, 'src', 'rider', 'main', 'resources', 'js', 'angelscript-language-server.js');
    let content = fs.readFileSync(bundledPath, 'utf8');
    
    // Replace the createConnection call to use stdin/stdout
    // The bundled code uses (0, node_1.createConnection) pattern
    content = content.replace(
      /node_1\.IPCMessageReader\(process\)/g,
      'node_1.StreamMessageReader(process.stdin)'
    );
    content = content.replace(
      /node_1\.IPCMessageWriter\(process\)/g,
      'node_1.StreamMessageWriter(process.stdout)'
    );
    
    // Also handle non-namespaced version just in case
    content = content.replace(
      /IPCMessageReader\(process\)/g,
      'StreamMessageReader(process.stdin)'
    );
    content = content.replace(
      /IPCMessageWriter\(process\)/g,
      'StreamMessageWriter(process.stdout)'
    );

    // Patch NotifyDiagnostics to normalize drive letter before sending to client
    // This ensures diagnostics from ALL sources (UpdateCompileDiagnostics, UpdateScriptModuleDiagnostics, etc.)
    // are normalized to lowercase drive letters before being sent to the LSP client.
    //
    // IMPORTANT: We only normalize the drive letter (D: -> d:), NOT the entire path.
    // The server's NormalizeUri() lowercases everything, which breaks case-sensitive paths.
    //
    // Original code:
    //   if (notifyEmpty || allDiagnostics.length != 0) {
    //     for (let func of NotifyFunctions)
    //       func(uri, allDiagnostics);
    //   }
    //
    // Patched code:
    //   if (notifyEmpty || allDiagnostics.length != 0) {
    //     let notifyUri = uri.replace(/^(file:\/\/\/)([A-Z])(:)/, (m, p, d, c) => p + d.toLowerCase() + c);
    //     for (let func of NotifyFunctions)
    //       func(notifyUri, allDiagnostics);
    //   }

    const notifyDiagnosticsPattern = /if \(notifyEmpty \|\| allDiagnostics\.length != 0\) \{\s*for \(let func of NotifyFunctions\)\s*func\(uri, allDiagnostics\);/;
    const notifyDiagnosticsReplacement =
      'if (notifyEmpty || allDiagnostics.length != 0) {\n        let notifyUri = uri.replace(/^(file:\\/\\/\\/)([A-Z])(:)/, (m, p, d, c) => p + d.toLowerCase() + c);\n        for (let func of NotifyFunctions)\n          func(notifyUri, allDiagnostics);';

    if (notifyDiagnosticsPattern.test(content)) {
      content = content.replace(notifyDiagnosticsPattern, notifyDiagnosticsReplacement);
      console.log('✓ Patched NotifyDiagnostics to normalize drive letters before notification');
    } else {
      console.warn('⚠ Warning: Could not find NotifyDiagnostics pattern to patch');
    }

    // Patch to defer initial Unreal connection until configuration is received
    // Original: connect_unreal() called immediately on startup with default port 27099
    // Patched: Set port to null initially, so first config always triggers connection
    //
    // This ensures the server uses the user-configured port from the start,
    // rather than connecting on 27099 and then reconnecting.

    // Step 1: Change initial port from 27099 to -1 (unconfigured)
    // Using -1 instead of null to ensure != comparison works reliably
    content = content.replace(
      /var port = 27099;/,
      'var port = -1;'
    );

    // Step 2: Comment out the immediate connect_unreal() call
    content = content.replace(
      /^connect_unreal\(\);$/m,
      '// connect_unreal(); // Patched: deferred until config received'
    );

    console.log('✓ Patched to defer Unreal connection until configuration received');

    fs.writeFileSync(bundledPath, content);
    console.log('✓ Language server bundled and patched for stdio communication!');
  } catch (error) {
    console.error('✗ Bundling failed:', error);
    process.exit(1);
  }
}

bundle();
