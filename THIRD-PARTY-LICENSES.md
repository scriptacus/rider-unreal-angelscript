# Third-Party Licenses

This project bundles and integrates code from third-party open source projects. Below are the licenses and attributions for these projects.

## vscode-unreal-angelscript

The AngelScript Language Server bundled with this plugin is derived from the [vscode-unreal-angelscript](https://github.com/Hazelight/vscode-unreal-angelscript) Visual Studio Code extension.

- **Project**: vscode-unreal-angelscript
- **Author**: Hazelight Games AB
- **License**: MIT License
- **Source**: https://github.com/Hazelight/vscode-unreal-angelscript
- **Bundled Location**: `src/rider/main/resources/js/angelscript-language-server.js`

### License Text

```
Copyright 2019 Hazelight Games AB

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
```

## Modifications

The language server has been modified from its original form:
- Bundled into a single JavaScript file using esbuild
- Communication protocol changed from IPC to stdio for compatibility with JetBrains Rider
- No modifications to the language server's core functionality or capabilities

---

Thank you to Hazelight Games AB and all contributors to the vscode-unreal-angelscript project for their excellent work on the AngelScript language server.
