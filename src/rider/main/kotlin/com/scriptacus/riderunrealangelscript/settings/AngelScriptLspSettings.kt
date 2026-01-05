package com.scriptacus.riderunrealangelscript.settings

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil

@State(
    name = "AngelScriptLspSettings",
    storages = [Storage("angelscript-lsp.xml")]
)
class AngelScriptLspSettings : PersistentStateComponent<AngelScriptLspSettings.State> {

    enum class CppNavigationStrategy {
        /** Navigate to C++ using text-based symbol search in Rider. Requires C++ sources in project. */
        TEXT_SEARCH {
            override fun toString() = "Rider";
        },
        /** Delegate C++ navigation to Unreal Engine. Opens in configured IDE via RiderLink. Works without C++ sources in Rider. */
        UNREAL_ENGINE {
            override fun toString() = "Unreal";
        }
    }

    data class State(
        // General
        var unrealConnectionPort: Int = 27099,
        var scriptIgnorePatterns: MutableList<String> = mutableListOf("**/Saved/**", "**/.plastic/**"),

        // Completion
        var mathCompletionShortcuts: Boolean = true,

        // Diagnostics
        var diagnosticsForUnrealNamingConvention: Boolean = true,
        var correctFloatLiteralsWhenExpectingDoublePrecision: Boolean = false,

        // Code Lenses
        var showCreateBlueprintClasses: MutableList<String> = mutableListOf("AActor", "UUserWidget"),

        // Navigation
        var cppNavigationStrategy: CppNavigationStrategy = CppNavigationStrategy.TEXT_SEARCH,

        // Advanced
        var projectCodeGenerationEnable: Boolean = false,

        // LSP Initialization
        var lspInitialDelayMs: Int = 2000,
        var lspMaxRetries: Int = 3,
        var lspRetryBackoffMs: Int = 1000
    )

    private var state = State()

    override fun getState(): State = state

    override fun loadState(state: State) {
        XmlSerializerUtil.copyBean(state, this.state)
    }

    fun notifySettingsChanged() {
        ApplicationManager.getApplication().messageBus
            .syncPublisher(AngelScriptSettingsListener.TOPIC)
            .settingsChanged(state)
    }

    companion object {
        fun getInstance(): AngelScriptLspSettings =
            ApplicationManager.getApplication().getService(AngelScriptLspSettings::class.java)
    }
}
