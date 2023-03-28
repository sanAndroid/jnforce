package com.github.sanandroid.jaleson.settings

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
        return mySettingsComponent!!.preferredFocusedComponent
    }

    override fun createComponent(): JComponent? {
        mySettingsComponent = JlsForceComponent()
        return mySettingsComponent!!.getPanel()
    }

    override fun isModified(): Boolean {
        val settings = JlsForceState.instance
        var modified = mySettingsComponent!!.userNameText != settings.userId
        modified = modified or (mySettingsComponent!!.passwordText != settings.password)
        modified = modified or (mySettingsComponent!!.clientIdText != settings.clientId)
        modified = modified or (mySettingsComponent!!.clientSecretText != settings.clientSecret)
        modified = modified or (mySettingsComponent!!.baseUrlText != settings.baseUrl)
        modified = modified or (mySettingsComponent!!.packageNameText != settings.packageName)
        modified = modified or (mySettingsComponent!!.classPathText != settings.classPath)
        modified = modified or (mySettingsComponent!!.filterLayoutable != settings.filterLayoutable)
        modified = modified or (mySettingsComponent!!.filterCreatable != settings.filterCreatable)
        modified = modified or (mySettingsComponent!!.filterInterfaces != settings.filterInterfaces)
        return modified
    }

    override fun apply() {
        val settings = JlsForceState.instance
        settings.userId = mySettingsComponent!!.userNameText
        settings.password = mySettingsComponent!!.passwordText
        settings.clientId = mySettingsComponent!!.clientIdText
        settings.clientSecret = mySettingsComponent!!.clientSecretText
        settings.baseUrl = mySettingsComponent!!.baseUrlText
        settings.packageName = mySettingsComponent!!.packageNameText
        settings.classPath = mySettingsComponent!!.classPathText
        settings.filterLayoutable = mySettingsComponent!!.filterLayoutable
        settings.filterCreatable = mySettingsComponent!!.filterCreatable
        settings.filterInterfaces = mySettingsComponent!!.filterInterfaces
    }

    /**
     * Not sure yet what to do with optionals here.
     */
    override fun reset() {
        val settings = JlsForceState.instance
        mySettingsComponent!!.userNameText = settings.userId
        mySettingsComponent!!.passwordText = settings.password ?: ""
        mySettingsComponent!!.clientIdText = settings.clientId
        mySettingsComponent!!.clientSecretText = settings.clientSecret ?: ""
        mySettingsComponent!!.packageNameText = settings.packageName ?: ""
        mySettingsComponent!!.classPathText = settings.classPath ?: ""
        mySettingsComponent!!.filterLayoutable = settings.filterLayoutable
        mySettingsComponent!!.filterCreatable = settings.filterCreatable
        mySettingsComponent!!.filterInterfaces = settings.filterInterfaces
    }

    override fun disposeUIResources() {
        mySettingsComponent = null
    }
}
