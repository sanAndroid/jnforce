package com.github.sanandroid.jlsforce.settings

import com.github.sanandroid.jlsforce.state.JlsForceSecureState
import com.github.sanandroid.jlsforce.state.JlsForceState
import com.intellij.openapi.options.Configurable
import org.jetbrains.annotations.Nls
import javax.swing.JComponent

/**
 * Provides controller functionality for application settings.
 */
class JlsForceConfigurable : Configurable {
    private var mySettingsComponent: JlsForceComponent? = null

    @Nls(capitalization = Nls.Capitalization.Title)
    override fun getDisplayName(): String {
        return "JLsForce Settings"
    }

    override fun getPreferredFocusedComponent(): JComponent? {
        return mySettingsComponent?.preferredFocusedComponent
    }

    override fun createComponent(): JComponent? {
        mySettingsComponent = JlsForceComponent()
        return mySettingsComponent?.getPanel()
    }

    override fun isModified(): Boolean {
        val settings = JlsForceState.instance
        val secureSettings = JlsForceSecureState.instance
        // var modified = mySettingsComponent?.userNameText != settings.username
        var modified = (mySettingsComponent?.clientIdText != settings.clientId)
        modified = modified or (mySettingsComponent?.baseUrlText != settings.baseUrl)
        modified = modified or (mySettingsComponent?.packageNameText != settings.packageName)
        modified = modified or (mySettingsComponent?.classPathText != settings.classPath)
        modified = modified or (mySettingsComponent?.filterCreatable != settings.filterCreatable)
        modified = modified or (mySettingsComponent?.filterCustom != settings.filterCustom)
        modified = modified or (mySettingsComponent?.filterDeletable != settings.filterDeletable)
        modified = modified or (mySettingsComponent?.filterMergeable != settings.filterMergeable)
        modified = modified or (mySettingsComponent?.filterLayoutable != settings.filterLayoutable)
        modified = modified or (mySettingsComponent?.filterReplicateable != settings.filterReplicateable)
        modified = modified or (mySettingsComponent?.filterRetrieveable != settings.filterRetrieveable)
        modified = modified or (mySettingsComponent?.filterSearchable != settings.filterSearchable)
        modified = modified or (mySettingsComponent?.filterUpdateable != settings.filterUpdateable)
        modified = modified or (mySettingsComponent?.useClassList != settings.useClassList)
        modified = modified or (mySettingsComponent?.useClassFilters != settings.useClassFilters)
        modified = modified or (mySettingsComponent?.classList != settings.classList)
        modified = modified or (mySettingsComponent?.clientSecretText != secureSettings.clientSecret)
        // modified = modified or (mySettingsComponent?.passwordText != secureSettings.password)
        // modified = modified or (mySettingsComponent?.securityTokenText != secureSettings.securityToken)
        return modified
    }

    override fun apply() {
        val settings = JlsForceState.instance
        val secureSettings = JlsForceSecureState.instance
        // settings.username = mySettingsComponent?.userNameText ?: ""
        settings.clientId = mySettingsComponent?.clientIdText ?: ""
        settings.baseUrl = mySettingsComponent?.baseUrlText ?: ""
        settings.packageName = mySettingsComponent?.packageNameText ?: ""
        settings.classPath = mySettingsComponent?.classPathText ?: ""
        settings.filterCreatable = mySettingsComponent?.filterCreatable ?: true
        settings.filterCustom = mySettingsComponent?.filterCustom ?: false
        settings.filterLayoutable = mySettingsComponent?.filterLayoutable ?: true
        settings.filterMergeable = mySettingsComponent?.filterMergeable ?: true
        settings.filterDeletable = mySettingsComponent?.filterDeletable ?: true
        settings.filterReplicateable = mySettingsComponent?.filterReplicateable ?: true
        settings.filterRetrieveable = mySettingsComponent?.filterRetrieveable ?: true
        settings.filterSearchable = mySettingsComponent?.filterSearchable ?: true
        settings.filterUpdateable = mySettingsComponent?.filterUpdateable ?: true


        settings.useClassList = mySettingsComponent?.useClassList ?: true
        settings.useClassFilters = mySettingsComponent?.useClassFilters ?: false
        settings.classList = mySettingsComponent?.classList ?: ""

        secureSettings.clientSecret = mySettingsComponent?.clientSecretText ?: ""
        // secureSettings.password = mySettingsComponent?.passwordText ?: ""
        // secureSettings.securityToken = mySettingsComponent?.securityTokenText ?: ""
    }

    override fun reset() {
        val settings = JlsForceState.instance
        val secureSettings = JlsForceSecureState.instance
        // mySettingsComponent!!.userNameText = settings.username
        mySettingsComponent!!.clientIdText = settings.clientId
        mySettingsComponent!!.packageNameText = settings.packageName ?: ""
        mySettingsComponent!!.classPathText = settings.classPath ?: ""
        mySettingsComponent!!.baseUrlText = settings.baseUrl
        mySettingsComponent!!.filterCreatable = settings.filterCreatable
        mySettingsComponent!!.filterCustom = settings.filterCustom
        mySettingsComponent!!.filterLayoutable = settings.filterLayoutable
        mySettingsComponent!!.filterMergeable = settings.filterMergeable
        mySettingsComponent!!.filterDeletable = settings.filterDeletable
        mySettingsComponent!!.filterReplicateable = settings.filterReplicateable
        mySettingsComponent!!.filterRetrieveable = settings.filterRetrieveable
        mySettingsComponent!!.filterSearchable = settings.filterSearchable
        mySettingsComponent!!.filterUpdateable = settings.filterUpdateable


        mySettingsComponent!!.useClassFilters = settings.useClassFilters
        mySettingsComponent!!.classList = settings.classList
        mySettingsComponent!!.useClassList = settings.useClassList

        mySettingsComponent!!.clientSecretText = secureSettings.clientSecret ?: ""
        // mySettingsComponent!!.passwordText = secureSettings.password ?: ""
        // mySettingsComponent!!.securityTokenText = secureSettings.securityToken ?: ""
    }

    override fun disposeUIResources() {
        mySettingsComponent = null
    }
}
