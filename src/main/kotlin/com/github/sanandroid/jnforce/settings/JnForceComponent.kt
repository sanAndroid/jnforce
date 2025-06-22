package com.github.sanandroid.jnforce.settings

import com.github.sanandroid.jnforce.services.SalesforceService
import com.intellij.ide.DataManager
import com.intellij.ide.actions.AboutPopup
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.ui.Messages
import com.intellij.ui.JBColor
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBPasswordField
import com.intellij.ui.components.JBRadioButton
import com.intellij.ui.components.JBTextArea
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.JBUI
import org.jetbrains.annotations.NotNull
import java.awt.Component
import java.awt.Dimension
import java.awt.GridBagLayout
import java.awt.GridLayout
import java.awt.event.ActionEvent
import javax.swing.AbstractAction
import javax.swing.BorderFactory
import javax.swing.Box
import javax.swing.BoxLayout
import javax.swing.ButtonGroup
import javax.swing.JButton
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.border.EmptyBorder

const val USERNAME = "username"
const val CLIENT_ID = "clientId"
const val CLIENT_SECRET = "clientSecret"
const val TEST_CONNECTION = "testconnection"
const val BASE_URL = "baseUrl"
const val PACKAGE_NAME = "packageName"
const val CLASS_PATH = "classPath"

const val CREATEABLE = "createable"
const val CUSTOM = "custom"
const val DELETABLE = "deletable"
const val LAYOUTABLE = "layoutable"
const val MERGEABLE = "mergeable"
const val REPLICATEABLE = "replicateable"
const val RETRIEVEABLE = "retrieveable"
const val SEARCHABLE = "searchable"
const val UPDATEABLE = "updateable"

const val USE_CLASS_LIST = "useClassList"
const val CLASS_LIST = "classList"
const val USE_CLASS_FILTERS = "useClassFilters"

/**
 * Supports creating and managing a [JPanel] for the Settings Dialog.
 */

class JnForceComponent {

    private lateinit var panel: JPanel
    private lateinit var filtersPanel: JPanel

    private val myUsernameText = JBTextField().apply {
        name = USERNAME
    }

    private val myClientIdText = JBTextField().apply {
        text = "Client ID"
        name = CLIENT_ID
    }

    private val myBaseUrlText = JBTextField().apply {
        text = "Base Url"
        name = BASE_URL
    }
    private val myPackageNameText = JBTextField().apply {
        name = PACKAGE_NAME
    }
    private val myClassPathText = JBTextField().apply {
        name = CLASS_PATH
    }
    private val myFilterCreatable = JBCheckBox("Only load creatable").apply {
        name = CREATEABLE
        isVisible = false
    }
    private val myFilterCustom = JBCheckBox("Only load custom").apply {
        name = CUSTOM
        isVisible = false
    }

    private val myFilterDeletable = JBCheckBox("Only load deletable").apply {
        name = DELETABLE
        isVisible = false
    }

    private val myFilterLayoutable = JBCheckBox("Only load layoutable").apply {
        name = LAYOUTABLE
        isVisible = false
    }

    private val myFilterMergeable = JBCheckBox("Only load mergeable").apply {
        name = MERGEABLE
        isVisible = false
    }
    private val myFilterReplicateable = JBCheckBox("Only load replicatable").apply {
        name = REPLICATEABLE
        isVisible = false
    }
    private val myFilterRetrievable = JBCheckBox("Only load retrievable").apply {
        name = RETRIEVEABLE
        isVisible = false
    }
    private val myFilterSearchable = JBCheckBox("Only load searchable").apply {
        name = SEARCHABLE
        isVisible = false
    }
    private val myFilterUpdateable = JBCheckBox("Only load updateable").apply {
        name = UPDATEABLE
        isVisible = false
    }

    private val myClientSecretText = JBPasswordField().apply {
        name = CLIENT_SECRET
    }

    private val myTestConnection = JButton("Test Connection").apply {
        layout = GridBagLayout();
        name = TEST_CONNECTION
        preferredSize = Dimension(80, 33)
        add(JBLabel("Test Connection"))
        text = "Test Connection"
        action = TestConnectionAction()
    }

    private val myUseClassFiltersButton = JBRadioButton("Use filters", false).apply {
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

    init {
        ButtonGroup().apply {
            add(myUseClassFiltersButton)
            add(myUseClassListButton)
        }
        buildPanel()
    }

    internal fun buildPanel() {
        panel = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            alignmentX = Component.LEFT_ALIGNMENT
            alignmentY = Component.TOP_ALIGNMENT
            val mainSettingsPanel = JPanel().apply {
                layout = GridLayout(0, 1, 10, 10)
                // layout = BoxLayout(this, BoxLayout.Y_AXIS)
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
                add(JBLabel("Test Connection"))
                add(myTestConnection)
                val classList = JPanel().apply {
                    layout = BoxLayout(this, BoxLayout.X_AXIS)
                    alignmentX = Component.LEFT_ALIGNMENT
                    alignmentY = Component.TOP_ALIGNMENT
                    add(JBLabel("Use Class List"))
                    add(Box.createHorizontalStrut(20))
                    add(myUseClassListButton)
                }
                add(classList)
                val filtersButton = JPanel().apply {
                    layout = BoxLayout(this, BoxLayout.X_AXIS)
                    alignmentX = Component.LEFT_ALIGNMENT
                    alignmentY = Component.TOP_ALIGNMENT
                    add(
                        JBLabel("Use Class Filters").apply {
                            alignmentX = Component.LEFT_ALIGNMENT
                            alignmentY = Component.TOP_ALIGNMENT
                        })
                    add(Box.createHorizontalStrut(20))
                    add(myUseClassFiltersButton.apply {
                        alignmentX = Component.LEFT_ALIGNMENT
                        alignmentY = Component.TOP_ALIGNMENT
                    })
                }
                add(filtersButton)
                val classTextFieldPanel = JPanel().apply {
                    alignmentX = Component.LEFT_ALIGNMENT
                    add(Box.createVerticalStrut(20))
                    add(Box.createHorizontalStrut(20))
                    add(myClassListTextField)
                }
                add(classTextFieldPanel)

            }
            add(mainSettingsPanel)
            filtersPanel = JPanel().apply {
                layout = BoxLayout(this, BoxLayout.X_AXIS)
                alignmentX = Component.LEFT_ALIGNMENT
                val leftFilterPanel = JPanel().apply {
                    alignmentX = Component.LEFT_ALIGNMENT
                    layout = BoxLayout(this, BoxLayout.Y_AXIS)
                    add(myFilterCreatable)
                    add(myFilterCustom)
                    add(myFilterDeletable)
                    add(myFilterLayoutable)
                    add(myFilterMergeable)
                }
                val rightFilterPanel = JPanel().apply {
                    alignmentX = Component.LEFT_ALIGNMENT
                    layout = BoxLayout(this, BoxLayout.Y_AXIS)
                    add(myFilterReplicateable)
                    add(myFilterRetrievable)
                    add(myFilterSearchable)
                    add(myFilterUpdateable)
                    add(Box.createHorizontalStrut(20))
                }
                add(leftFilterPanel)
                add(rightFilterPanel)
            }
            add(filtersPanel)
            filterOrListVisible()
        }
    }

    private fun filterOrListVisible() {
        if (myUseClassListButton.isSelected) {
            filtersPanel.isVisible = false
            myClassListTextField.isVisible = true
        }
        if (myUseClassFiltersButton.isSelected) {
            myClassListTextField.isVisible = false
            filtersPanel.isVisible = true
        }
    }

    fun getPanel() = panel

    fun getPreferredFocusedComponent() = myUsernameText

    val preferredFocusedComponent: JComponent
        get() = myUsernameText

    var clientIdText: String
        get() = myClientIdText.text
        set(newText) {
            myClientIdText.text = newText
        }

    var clientSecretText: String
        get() = myClientSecretText.password.joinToString("")
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

    var filterCreatable: Boolean
        get() = myFilterCreatable.isSelected
        set(newStatus) {
            myFilterCreatable.isSelected = newStatus
        }
    var filterCustom: Boolean
        get() = myFilterCustom.isSelected
        set(newStatus) {
            myFilterCustom.isSelected = newStatus
        }

    var filterDeletable: Boolean
        get() = myFilterDeletable.isSelected
        set(newStatus) {
            myFilterDeletable.isSelected = newStatus
        }
    var filterLayoutable: Boolean
        get() = myFilterLayoutable.isSelected
        set(newStatus) {
            myFilterCustom.isSelected = newStatus
        }

    var filterMergeable: Boolean
        get() = myFilterMergeable.isSelected
        set(newStatus) {
            myFilterMergeable.isSelected = newStatus
        }
    var filterReplicateable: Boolean
        get() = myFilterReplicateable.isSelected
        set(newStatus) {
            myFilterReplicateable.isSelected = newStatus
        }
    var filterRetrieveable: Boolean
        get() = myFilterRetrievable.isSelected
        set(newStatus) {
            myFilterRetrievable.isSelected = newStatus
        }
    var filterSearchable: Boolean
        get() = myFilterSearchable.isSelected
        set(newStatus) {
            myFilterSearchable.isSelected = newStatus
        }
    var filterUpdateable: Boolean
        get() = myFilterUpdateable.isSelected
        set(newStatus) {
            myFilterUpdateable.isSelected = newStatus
        }

    var useClassList: Boolean
        get() = myUseClassListButton.isSelected
        set(newStatus) {
            myUseClassListButton.isSelected = newStatus
            filterOrListVisible()
        }
    var useClassFilters: Boolean
        get() = myUseClassFiltersButton.isSelected
        set(newStatus) {
            myUseClassFiltersButton.isSelected = newStatus
            filterOrListVisible()
        }
    var classList: String
        get() = myClassListTextField.text
        set(newStatus) {
            myClassListTextField.text = newStatus
        }

    inner class TestConnectionAction : AbstractAction() {
        override fun actionPerformed(@NotNull e: ActionEvent): Unit = with(panel) {
            val project = CommonDataKeys.PROJECT.getData(DataManager.getInstance().getDataContext(this))

            project?.let {
                val success = SalesforceService.instance(project).testConnection()
                if (success) {
                    Messages.showInfoMessage(
                        project, "Successfully connected to Salesforce.", "Connection Successful"
                    )
                } else {
                    Messages.showErrorDialog(
                        project,
                        "Authentication failed. Please check your credentials and try again.",
                        "Connection Failed"
                    )
                }
            }
            Messages.showErrorDialog(
                "Authentication failed. Please check your credentials and try again.",
                "Connection Failed"
            )
        }
    }

    inner class UseClassFilterAction : AbstractAction() {
        override fun actionPerformed(@NotNull e: ActionEvent) = with(panel) {
            // layout = GridLayout(0, 1)
            myFilterCreatable.isVisible = true
            myFilterCustom.isVisible = true
            myFilterDeletable.isVisible = true
            myFilterMergeable.isVisible = true
            myFilterLayoutable.isVisible = true
            myFilterReplicateable.isVisible = true
            myFilterRetrievable.isVisible = true
            myFilterSearchable.isVisible = true
            myFilterUpdateable.isVisible = true

            myClassListTextField.isVisible = false
            revalidate()
            repaint()
        }
    }

    inner class UseClassListAction : AbstractAction() {
        override fun actionPerformed(@NotNull e: ActionEvent) = with(panel) {
            myFilterCreatable.isVisible = false
            myFilterCustom.isVisible = false
            myFilterDeletable.isVisible = false
            myFilterMergeable.isVisible = false
            myFilterLayoutable.isVisible = false
            myFilterReplicateable.isVisible = false
            myFilterRetrievable.isVisible = false
            myFilterSearchable.isVisible = false
            myFilterUpdateable.isVisible = false

            myClassListTextField.isVisible = true
            revalidate()
            repaint()
        }
    }
}
