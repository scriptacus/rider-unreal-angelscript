package com.scriptacus.riderunrealangelscript.lsp

import com.intellij.openapi.project.Project
import com.redhat.devtools.lsp4ij.client.LanguageClientImpl

/**
 * Custom language client for AngelScript.
 */
class AngelScriptLanguageClient(project: Project) : LanguageClientImpl(project)
