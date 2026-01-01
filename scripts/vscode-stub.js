/**
 * Minimal stub for 'vscode' module to allow the debug adapter to run standalone.
 * The debug adapter uses vscode.workspace for configuration and workspace folders.
 * This stub provides minimal implementations that work outside of VSCode.
 */

const workspace = {
    getConfiguration: function(section) {
        // Return a configuration object with sensible defaults
        return {
            get: function(key, defaultValue) {
                return defaultValue;
            }
        };
    },
    workspaceFolders: []
};

module.exports = {
    workspace
};
