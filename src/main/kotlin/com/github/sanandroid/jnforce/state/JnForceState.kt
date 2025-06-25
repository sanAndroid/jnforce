package com.github.sanandroid.jnforce.state

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil
import org.jetbrains.annotations.Nullable


@State(
    name = "com.github.sanandroid.jnforce.settings.JlsForceState",
    storages = [Storage("JlsForcePlugin.xml")],
)
class JnForceState : PersistentStateComponent<JnForceState?> {

    @Nullable
    override fun getState() = this

    override fun loadState(state: JnForceState) =
        XmlSerializerUtil.copyBean(state, this)

    // var username = ""
    var clientId = ""

    var baseUrl = ""

    var filterLayoutable = false
    var filterCreatable = true
    var filterCustom = false
    var filterDeletable = true
    var filterMergeable = true
    var filterReplicateable = true
    var filterRetrieveable = true
    var filterSearchable = true
    var filterUpdateable = true

    var classList = ""
    var useClassFilters: Boolean = false
    var useClassList: Boolean = true

    companion object {
        val instance: JnForceState
            get() = ApplicationManager.getApplication().getService(JnForceState::class.java)
    }
}
