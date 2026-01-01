package com.scriptacus.riderunrealangelscript.settings

import com.intellij.util.messages.Topic

interface AngelScriptSettingsListener {
    fun settingsChanged(state: AngelScriptLspSettings.State)

    companion object {
        val TOPIC = Topic.create(
            "AngelScript Settings Changed",
            AngelScriptSettingsListener::class.java
        )
    }
}
