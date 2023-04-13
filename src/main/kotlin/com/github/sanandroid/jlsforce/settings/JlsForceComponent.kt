package com.github.sanandroid.jlsforce.settings

import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBPasswordField
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.FormBuilder
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.JPasswordField

const val USERNAME = "username"
const val PASSWORD = "password"
const val CLIENT_ID = "clientId"
const val CLIENT_SECRET = "clientSecret"
const val BASE_URL = "baseUrl"
const val PACKAGE_NAME = "packageName"
const val CLASS_PATH = "classPath"
const val LAYOUTABLE = "layoutable"
const val CREATABLE = "creatable"
const val INTERFACES = "interfaces"

/**
 * Supports creating and managing a [JPanel] for the Settings Dialog.
 */

class JlsForceComponent {
    private val panel: JPanel
    private val myUsernameText = JBTextField().apply {
        name = USERNAME
    }
    private val myPasswordText = JPasswordField().apply {
        name = PASSWORD
    }
    private val myClientIdText = JBTextField().apply {
        name = CLIENT_ID
    }
    private val myClientSecretText = JBPasswordField().apply {
        name = CLIENT_SECRET
    }
    private val myBaseUrlText = JBTextField().apply {
        name = BASE_URL
    }
    private val myPackageNameText = JBTextField().apply {
        name = PACKAGE_NAME
    }
    private val myClassPathText = JBTextField().apply {
        name = CLASS_PATH
    }
    private val myFilterLayoutable = JBCheckBox("Only load layoutable").apply {
        name = LAYOUTABLE
    }
    private val myFilterCreatable = JBCheckBox("Only load creatable").apply {
        name = CREATABLE
    }
    private val myFilterInterfaces = JBCheckBox("Don't load interfaces").apply {
        name = INTERFACES
    }

    init {
        panel = FormBuilder.createFormBuilder()
            .addLabeledComponent(JBLabel("User name: "), myUsernameText, 1, false)
            .addLabeledComponent(JBLabel("Password"), myPasswordText, 1, false)
            .addLabeledComponent(JBLabel("ClientId"), myClientIdText, 1, false)
            .addLabeledComponent(JBLabel("ClientSecret"), myClientSecretText, 1, false)
            .addLabeledComponent(JBLabel("Package name"), myPackageNameText, 1, false)
            .addLabeledComponent(JBLabel("Class path"), myClassPathText, 1, false)
            .addLabeledComponent(JBLabel("Base Url"), myBaseUrlText, 1, false)
            .addLabeledComponent(JBLabel(""), myFilterLayoutable, 1, false)
            .addLabeledComponent(JBLabel(""), myFilterCreatable, 1, false)
            .addLabeledComponent(JBLabel(""), myFilterInterfaces, 1, false)
            .addComponentFillVertically(JPanel(), 0)
            .panel
    }

    fun getPanel() = panel

    fun getPreferredFocusedComponent() = myUsernameText

    val preferredFocusedComponent: JComponent
        get() = myUsernameText

    var userNameText: String
        get() = myUsernameText.text
        set(newText) {
            myUsernameText.text = newText
        }

    var passwordText: String
        get() = myPasswordText.text
        set(newText) {
            myPasswordText.text = newText
        }

    var clientIdText: String
        get() = myClientIdText.text
        set(newText) {
            myClientIdText.text = newText
        }

    var clientSecretText: String
        get() = myClientSecretText.text
        set(newText) {
            myClientSecretText.text = newText
        }

    var baseUrlText: String
        get() = myBaseUrlText.text
        set(newText) {
            myBaseUrlText.text = newText
        }

    var packageNameText: String
        get() = myPackageNameText.text
        set(newText) {
            myPackageNameText.text = newText
        }
    var classPathText: String
        get() = myClassPathText.text
        set(newText) {
            myClassPathText.text = newText
        }

    var filterLayoutable: Boolean
        get() = myFilterLayoutable.isSelected
        set(newStatus) {
            myFilterInterfaces.isSelected = newStatus
        }

    var filterCreatable: Boolean
        get() = myFilterCreatable.isSelected
        set(newStatus) {
            myFilterCreatable.isSelected = newStatus
        }

    var filterInterfaces: Boolean
        get() = myFilterInterfaces.isSelected
        set(newStatus) {
            myFilterInterfaces.isSelected = newStatus
        }
}
