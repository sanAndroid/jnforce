package com.github.sanandroid.jlsforce.services

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil
import org.jetbrains.annotations.Nullable

/**
 * Supports storing the application settings in a persistent way.
 * The [State] and [Storage] annotations define the name of the data and the file name where
 * these persistent application settings are stored.
 */
@State(
    name = "com.github.sanandroid.jlsforce.settings.JlsForceState",
    storages = [Storage("SdkSettingsPlugin.xml")],
)
class JlsForceState : PersistentStateComponent<JlsForceState?> {

    @Nullable
    override fun getState() = this

    override fun loadState(state: JlsForceState) =
        XmlSerializerUtil.copyBean(state, this)

    var username = ""
    var clientId = ""

    var baseUrl = ""
    var classPath: String? = null
    var packageName: String? = null

    var filterLayoutable = true
    var filterCreatable = true
    var filterInterfaces = false

    companion object {
        val instance: JlsForceState
            get() = ApplicationManager.getApplication().getService(JlsForceState::class.java)
    }
}
