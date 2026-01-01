const esbuild = require('esbuild');
const path = require('path');
const fs = require('fs');

// Project root is one level up from scripts/
const projectRoot = path.join(__dirname, '..');

// Plugin to replace vscode imports with our stub
const vscodeStubPlugin = {
  name: 'vscode-stub',
  setup(build) {
    const vstubPath = path.join(__dirname, 'vscode-stub.js');

    build.onResolve({ filter: /^vscode$/ }, args => {
      return { path: vstubPath };
    });
  }
};

async function bundle() {
  try {
    // Bundle the debug adapter
    await esbuild.build({
      entryPoints: [path.join(projectRoot, 'third-party', 'vscode-unreal-angelscript', 'extension', 'src', 'debugAdapter.ts')],
      bundle: true,
      outfile: path.join(projectRoot, 'src', 'rider', 'main', 'resources', 'js', 'angelscript-debug-adapter.js'),
      platform: 'node',
      target: 'node14',
      format: 'cjs',
      plugins: [vscodeStubPlugin],
      minify: false,
      sourcemap: false,
      logLevel: 'info',
    });

    console.log('✓ Debug adapter bundled successfully!');
  } catch (error) {
    console.error('✗ Bundling failed:', error);
    process.exit(1);
  }
}

bundle();