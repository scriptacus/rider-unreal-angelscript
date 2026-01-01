package com.scriptacus.riderunrealangelscript.debug

import com.intellij.codeInsight.hints.ProviderInfo
import com.intellij.codeInsight.hints.declarative.InlayHintsProviderFactory
import com.intellij.codeInsight.hints.declarative.InlayOptionInfo
import com.intellij.codeInsight.hints.declarative.InlayProviderInfo
import com.intellij.codeInsight.hints.declarative.InlayProviderOption
import com.intellij.lang.Language
import com.scriptacus.riderunrealangelscript.lang.AngelScriptLanguage

/**
 * Factory for creating AngelScript debug inlay hints provider.
 *
 * Registers the debug inline values provider for AngelScript files.
 */
class AngelScriptDebugInlayHintsProviderFactory : InlayHintsProviderFactory {

    override fun getProviderInfo(language: Language, providerId: String): InlayProviderInfo? {
        if (language != AngelScriptLanguage.INSTANCE) {
            return null
        }
        if (providerId != AngelScriptDebugInlayHintsProvider.PROVIDER_ID) {
            return null
        }

        return createProviderInfo()
    }

    override fun getProvidersForLanguage(language: Language): List<InlayProviderInfo> {
        if (language != AngelScriptLanguage.INSTANCE) {
            return emptyList()
        }
        return listOf(createProviderInfo())
    }

    override fun getSupportedLanguages(): Set<Language> {
        return setOf(AngelScriptLanguage.INSTANCE)
    }

    private fun createProviderInfo(): InlayProviderInfo {
        return InlayProviderInfo(
            AngelScriptDebugInlayHintsProvider(),
            AngelScriptDebugInlayHintsProvider.PROVIDER_ID,
            emptySet<InlayOptionInfo>(),
            true,
            "AngelScript Debug Values");

//        return InlayProviderInfo(
//            providerId = AngelScriptDebugInlayHintsProvider.PROVIDER_ID,
//            name = "Debug Values",
//            description = "Shows variable values inline during debugging",
//            group = "Debugger",
//            options = emptyList<InlayProviderOption>(),
//            provider = AngelScriptDebugInlayHintsProvider()
//        )
    }
}
