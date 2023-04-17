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
    var mySettingsComponent: JlsForceComponent? = null

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
        var modified = mySettingsComponent?.userNameText != settings.username
        modified = modified or (mySettingsComponent?.clientIdText != settings.clientId)
        modified = modified or (mySettingsComponent?.baseUrlText != settings.baseUrl)
        modified = modified or (mySettingsComponent?.packageNameText != settings.packageName)
        modified = modified or (mySettingsComponent?.classPathText != settings.classPath)
        modified = modified or (mySettingsComponent?.filterLayoutable != settings.filterLayoutable)
        modified = modified or (mySettingsComponent?.filterCreatable != settings.filterCreatable)
        modified = modified or (mySettingsComponent?.filterInterfaces != settings.filterInterfaces)
        modified = modified or (mySettingsComponent?.useClassList != settings.useClassList)
        modified = modified or (mySettingsComponent?.useClassFilters != settings.useClassFilters)
        modified = modified or (mySettingsComponent?.classList != settings.classList)
        modified = modified or (mySettingsComponent?.clientSecretText != secureSettings.clientSecret)
        modified = modified or (mySettingsComponent?.passwordText != secureSettings.password)
        return modified
    }

    override fun apply() {
        val settings = JlsForceState.instance
        val secureSettings = JlsForceSecureState.instance
        settings.username = mySettingsComponent?.userNameText ?: ""
        settings.clientId = mySettingsComponent?.clientIdText ?: ""
        settings.baseUrl = mySettingsComponent?.baseUrlText ?: ""
        settings.packageName = mySettingsComponent?.packageNameText ?: ""
        settings.classPath = mySettingsComponent?.classPathText ?: ""
        settings.filterLayoutable = mySettingsComponent?.filterLayoutable ?: true
        settings.filterCreatable = mySettingsComponent?.filterCreatable ?: true
        settings.filterInterfaces = mySettingsComponent?.filterInterfaces ?: false

        settings.useClassList = mySettingsComponent?.useClassList ?: true
        settings.useClassFilters = mySettingsComponent?.useClassFilters ?: false
        settings.classList = mySettingsComponent?.classList ?: ""

        secureSettings.clientSecret = mySettingsComponent?.clientSecretText ?: ""
        secureSettings.password = mySettingsComponent?.passwordText ?: ""
    }

    override fun reset() {
        val settings = JlsForceState.instance
        val secureSettings = JlsForceSecureState.instance
        mySettingsComponent!!.userNameText = settings.username
        mySettingsComponent!!.clientIdText = settings.clientId
        mySettingsComponent!!.packageNameText = settings.packageName ?: ""
        mySettingsComponent!!.classPathText = settings.classPath ?: ""
        mySettingsComponent!!.baseUrlText = settings.baseUrl
        mySettingsComponent!!.filterLayoutable = settings.filterLayoutable
        mySettingsComponent!!.filterCreatable = settings.filterCreatable
        mySettingsComponent!!.filterInterfaces = settings.filterInterfaces

        mySettingsComponent!!.useClassFilters != settings.useClassFilters
        mySettingsComponent!!.classList != settings.classList
        mySettingsComponent!!.useClassList != settings.useClassList

        mySettingsComponent!!.clientSecretText = secureSettings.clientSecret ?: ""
        mySettingsComponent!!.passwordText = secureSettings.password ?: ""
    }

    override fun disposeUIResources() {
        mySettingsComponent = null
    }
}
