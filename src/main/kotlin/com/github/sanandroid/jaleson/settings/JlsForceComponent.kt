package com.github.sanandroid.jaleson.settings

import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBPasswordField
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.FormBuilder
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.JPasswordField

/**
 * Supports creating and managing a [JPanel] for the Settings Dialog.
 */

class JlsForceComponent {
    private val panel: JPanel
    private val myUserNameText = JBTextField()
    private val myPasswordText = JPasswordField()
    private val myClientIdText = JBTextField()
    private val myClientSecretText = JBPasswordField()
    private val myBaseUrlText = JBTextField()
    private val myPackageNameText = JBTextField()
    private val myClassPathText = JBTextField()
    private val myFilterLayoutable = JBCheckBox("Only load layoutable")
    private val myFilterCreatable = JBCheckBox("Only load createable")
    private val myFilterInterfaces = JBCheckBox("Don't load interfaces")

    init {
        panel = FormBuilder.createFormBuilder()
            .addLabeledComponent(JBLabel("User name: "), myUserNameText, 1, false)
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

    fun getPreferredFocusedComponent() = myUserNameText

    val preferredFocusedComponent: JComponent
        get() = myUserNameText

    var userNameText: String
        get() = myUserNameText.text
        set(newText) {
            myUserNameText.text = newText
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
