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

    // Patch to add C++ navigation request handlers
    // These handlers allow the Rider plugin to get C++ symbol info and navigate to C++ code
    const cppNavigationHandlers = `
connection.onRequest("angelscript/getCppSymbol", (params) => {
  let uri = params.uri;
  let position = params.position;
  connection.console.log(\`[getCppSymbol] Request received - URI: \${uri}, Position: \${position.line}:\${position.character}\`);
  let asmodule = GetAndParseModule(uri);
  if (!asmodule) {
    connection.console.log(\`[getCppSymbol] Module not found for URI: \${uri}\`);
    return null;
  }
  if (!asmodule.resolved) {
    connection.console.log(\`[getCppSymbol] Module not resolved: \${uri}\`);
    return null;
  }
  connection.console.log(\`[getCppSymbol] Module found and resolved: \${asmodule.modulename}\`);
  let cppSymbol = scriptsymbols.GetCppSymbol(asmodule, position);
  if (!cppSymbol) {
    connection.console.log(\`[getCppSymbol] No C++ symbol found at position \${position.line}:\${position.character}\`);
    return null;
  }
  connection.console.log(\`[getCppSymbol] C++ symbol found: \${cppSymbol[0]}.\${cppSymbol[1]}\`);
  return {
    className: cppSymbol[0],
    symbolName: cppSymbol[1]
  };
});
connection.onRequest("angelscript/navigateToCpp", (params) => {
  let uri = params.uri;
  let position = params.position;
  connection.console.log(\`[navigateToCpp] Request received - URI: \${uri}, Position: \${position.line}:\${position.character}\`);
  let asmodule = GetAndParseModule(uri);
  if (!asmodule) {
    connection.console.log(\`[navigateToCpp] Module not found for URI: \${uri}\`);
    return false;
  }
  if (!asmodule.resolved) {
    connection.console.log(\`[navigateToCpp] Module not resolved: \${uri}\`);
    return false;
  }
  connection.console.log(\`[navigateToCpp] Module found and resolved: \${asmodule.modulename}\`);
  let cppSymbol = scriptsymbols.GetCppSymbol(asmodule, position);
  if (!cppSymbol) {
    connection.console.log(\`[navigateToCpp] No C++ symbol found at position \${position.line}:\${position.character}\`);
    return false;
  }
  connection.console.log(\`[navigateToCpp] C++ symbol found: \${cppSymbol[0]}.\${cppSymbol[1]}\`);
  if (unreal) {
    connection.console.log(\`[navigateToCpp] Sending goto command to Unreal Engine\`);
    unreal.write(buildGoTo(cppSymbol[0], cppSymbol[1]));
    return true;
  }
  connection.console.log(\`[navigateToCpp] Not connected to Unreal Engine\`);
  return false;
});
`;

    // Find the location to insert the handlers (after angelscript/getAPIDetails)
    // Match the complete getAPIDetails handler ending with "return promise;\n});"
    // Use a flag to ensure we only add once
    let handlerAdded = false;
    const insertionPoint = /connection\.onRequest\("angelscript\/getAPIDetails"[\s\S]*?return promise;\s*\}\);/;
    content = content.replace(insertionPoint, (match) => {
      if (!handlerAdded) {
        handlerAdded = true;
        return match + cppNavigationHandlers;
      }
      return match;
    });

    if (handlerAdded) {
      console.log('✓ Patched to add C++ navigation request handlers');
    } else {
      console.warn('⚠ Warning: Could not find insertion point for C++ navigation handlers');
    }

    fs.writeFileSync(bundledPath, content);
    console.log('✓ Language server bundled and patched for stdio communication!');
  } catch (error) {
    console.error('✗ Bundling failed:', error);
    process.exit(1);
  }
}

bundle();
