package com.github.sanandroid.jnforce.settings

import com.github.sanandroid.jnforce.state.JnForceSecureState
import com.github.sanandroid.jnforce.state.JnForceState
import com.intellij.testFramework.TestDataPath
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBRadioButton
import com.intellij.ui.components.JBTextArea
import com.intellij.ui.components.JBTextField
import javax.swing.JPasswordField

@TestDataPath("\$CONTENT_ROOT/src/test/testData")
class JlsForceSettingsTest : LightJavaCodeInsightFixtureTestCase() {

    fun testClientIdIsUpdated() {
        correctComponentIsUpdated(CLIENT_ID)
        assert(JnForceState.instance.clientId == CLIENT_ID)
    }

    fun testClientSecretIsUpdated() {
        JnForceState.instance.clientId = "clientId"
        correctComponentIsUpdated(CLIENT_SECRET)
        assert(JnForceSecureState.instance.clientSecret == CLIENT_SECRET)
    }

    fun testBaserURlIsUpdated() {
        correctComponentIsUpdated(BASE_URL)
        assert(JnForceState.instance.baseUrl == BASE_URL)
    }

    fun testPackageName() {
        correctComponentIsUpdated(PACKAGE_NAME)
        assert(JnForceState.instance.packageName == PACKAGE_NAME)
    }

    fun testClassPath() {
        correctComponentIsUpdated(CLASS_PATH)
        assert(JnForceState.instance.classPath == CLASS_PATH)
    }

    private fun activateClassFilters() =
        JnForceState.instance.apply {
            useClassFilters = true
            useClassList = false
        }

    fun testCreatable() {
        activateClassFilters()
        correctComponentIsUpdated(CREATEABLE, "false")
        assert(!JnForceState.instance.filterCreatable)
    }

    fun testCustom() {
        activateClassFilters()
        correctComponentIsUpdated(CUSTOM, "false")
        assert(!JnForceState.instance.filterCustom)
    }

    fun testDeletable() {
        activateClassFilters()
        correctComponentIsUpdated(DELETABLE, "false")
        assert(!JnForceState.instance.filterDeletable)
    }

    fun testMergeable() {
        activateClassFilters()
        correctComponentIsUpdated(MERGEABLE, "false")
        assert(!JnForceState.instance.filterMergeable)
    }

    fun testLayoutable() {
        activateClassFilters()
        correctComponentIsUpdated(LAYOUTABLE, "false")
        assert(!JnForceState.instance.filterLayoutable)
    }

    fun testReplicateable() {
        activateClassFilters()
        correctComponentIsUpdated(REPLICATEABLE, "false")
        assert(!JnForceState.instance.filterReplicateable)
    }

    fun testRetrieveable() {
        activateClassFilters()
        correctComponentIsUpdated(RETRIEVEABLE, "false")
        assert(!JnForceState.instance.filterRetrieveable)
    }

   fun testSearchable() {
        activateClassFilters()
        correctComponentIsUpdated(SEARCHABLE, "false")
        assert(!JnForceState.instance.filterSearchable)
    }

    fun testUpdateable() {
        activateClassFilters()
        correctComponentIsUpdated(UPDATEABLE, "false")
        assert(!JnForceState.instance.filterUpdateable)
    }

    fun testClassTextfield() {
        correctComponentIsUpdated(CLASS_LIST)
        assert(JnForceState.instance.classList == CLASS_LIST)
    }

    fun testUseClassList() {
        correctComponentIsUpdated(USE_CLASS_LIST, "true")
        assert(JnForceState.instance.useClassList)
        assert(!JnForceState.instance.useClassFilters)
    }

    fun testSelectUseClassFilters() {
        correctComponentIsUpdated(USE_CLASS_FILTERS, "true")
        assert(JnForceState.instance.useClassFilters)
        assert(!JnForceState.instance.useClassList)
    }

    private fun correctComponentIsUpdated(
        componentName: String,
        setTo: String = componentName,
    ) {
        val jlsForceConfigurable = JlsForceConfigurable()
        val panel = jlsForceConfigurable.createComponent()
        when (val jComponent = (panel?.components?.find { it.name == componentName }!!)) {
            is JBCheckBox -> jComponent.isSelected = setTo.toBoolean()
            is JBRadioButton -> jComponent.isSelected = setTo.toBoolean()
            is JBTextArea -> jComponent.text = setTo
            is JBTextField -> jComponent.text = setTo
            is JPasswordField -> {
                (panel.components?.find { it.name == CLIENT_ID }!! as JBTextField).text = CLIENT_ID
                (panel.components?.find { it.name == USERNAME }!! as JBTextField).text = USERNAME
                jComponent.text = setTo
            }
        }
        jlsForceConfigurable.apply()
    }
}
