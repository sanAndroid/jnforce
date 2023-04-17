package com.github.sanandroid.jlsforce.settings

import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBPasswordField
import com.intellij.ui.components.JBRadioButton
import com.intellij.ui.components.JBTextArea
import com.intellij.ui.components.JBTextField
import org.jetbrains.annotations.NotNull
import java.awt.GridLayout
import java.awt.event.ActionEvent
import javax.swing.AbstractAction
import javax.swing.ButtonGroup
import javax.swing.JComponent
import javax.swing.JPanel

const val USERNAME = "username"
const val PASSWORD = "password"
const val CLIENT_ID = "clientId"
const val CLIENT_SECRET = "clientSecret"
const val BASE_URL = "baseUrl"
const val PACKAGE_NAME = "packageName"
const val CLASS_PATH = "classPath"
const val LAYOUTABLE = "filterLayoutable"
const val CREATABLE = "filterCreatable"
const val INTERFACES = "filterInterfaces"
const val USE_CLASS_LIST = "useClassList"
const val CLASS_LIST = "classList"
const val USE_CLASS_FILTERS = "useClassFilters"

/**
 * Supports creating and managing a [JPanel] for the Settings Dialog.
 */

class JlsForceComponent {

    private lateinit var panel: JPanel

    private val myUsernameText = JBTextField().apply {
        name = USERNAME
    }

    private val myClientIdText = JBTextField().apply {
        name = CLIENT_ID
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
        isVisible = false
    }
    private val myFilterCreatable = JBCheckBox("Only load creatable").apply {
        name = CREATABLE
        isVisible = false
    }
    private val myFilterInterfaces = JBCheckBox("Don't load interfaces").apply {
        name = INTERFACES
        isVisible = false
    }
    private val myPasswordText = JBPasswordField().apply {
        name = PASSWORD
    }
    private val myClientSecretText = JBPasswordField().apply {
        name = CLIENT_SECRET
    }
    private val myUseClassFiltersButton = JBRadioButton("Use filters").apply {
        name = USE_CLASS_FILTERS
        action = UseClassFilterAction()
    }

    private val myUseClassListButton = JBRadioButton("Use list", true).apply {
        name = USE_CLASS_LIST
        action = UseClassListAction()
    }

    private val myClassListTextField = JBTextArea().apply {
        name = CLASS_LIST
        isVisible = false
    }

    private val myUseClassListButtonGroup = ButtonGroup().apply {
        add(myUseClassFiltersButton)
        add(myUseClassListButton)
    }

    init {
        buildPanel()
    }

    internal fun buildPanel() {
        panel = JPanel().apply {
            layout = GridLayout(0, 1)
            add(JBLabel("User name: "))
            add(myUsernameText)
            add(JBLabel("Password"))
            add(myPasswordText)
            add(JBLabel("ClientId"))
            add(myClientIdText)
            add(JBLabel("ClientSecret"))
            add(myClientSecretText)
            add(JBLabel("Package name"))
            add(myPackageNameText)
            add(JBLabel("Class path"))
            add(myClassPathText)
            add(JBLabel("Base url"))
            add(myBaseUrlText)
            add(JBLabel("Use class list"))
            add(myUseClassListButton)
            add(JBLabel("Use filter"))
            add(myUseClassFiltersButton)
            add(myClassListTextField)
            add(myFilterCreatable)
            add(myFilterInterfaces)
            add(myFilterLayoutable)
            if (myUseClassListButton.isSelected) {
                myClassListTextField.isVisible = true
            }
            if (myUseClassFiltersButton.isSelected) {
                myFilterCreatable.isVisible = true
                myFilterInterfaces.isVisible = true
                myFilterLayoutable.isVisible = true
            }
        }
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
    var useClassList: Boolean
        get() = myUseClassListButton.isSelected
        set(newStatus) {
            myUseClassListButton.isSelected = newStatus
        }
    var useClassFilters: Boolean
        get() = myUseClassFiltersButton.isSelected
        set(newStatus) {
            myUseClassFiltersButton.isSelected = newStatus
        }
    var classList: String
        get() = myClassListTextField.text
        set(newStatus) {
            myClassListTextField.text = newStatus
        }

    inner class UseClassFilterAction : AbstractAction() {
        override fun actionPerformed(@NotNull e: ActionEvent) = with(panel) {
            // layout = GridLayout(0, 1)
            myFilterLayoutable.isVisible = true
            myFilterCreatable.isVisible = true
            myFilterInterfaces.isVisible = true
            myClassListTextField.isVisible = false
            revalidate()
            repaint()
        }
    }

    inner class UseClassListAction : AbstractAction() {
        override fun actionPerformed(@NotNull e: ActionEvent) = with(panel) {
            myFilterLayoutable.isVisible = false
            myFilterCreatable.isVisible = false
            myFilterInterfaces.isVisible = false
            myClassListTextField.isVisible = true
            revalidate()
            repaint()
        }
    }
}
