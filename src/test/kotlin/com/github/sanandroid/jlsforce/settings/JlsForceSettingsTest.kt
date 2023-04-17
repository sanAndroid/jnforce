package com.github.sanandroid.jlsforce.settings

import com.github.sanandroid.jlsforce.state.JlsForceSecureState
import com.github.sanandroid.jlsforce.state.JlsForceState
import com.intellij.testFramework.TestDataPath
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBRadioButton
import com.intellij.ui.components.JBTextArea
import com.intellij.ui.components.JBTextField
import javax.swing.JPasswordField

@TestDataPath("\$CONTENT_ROOT/src/test/testData")
class JlsForceSettingsTest : LightJavaCodeInsightFixtureTestCase() {

    fun testUserNameIsUpdated() {
        correctComponentIsUpdated(USERNAME)
        assert(JlsForceState.instance.username == USERNAME)
    }

    fun testClientIdIsUpdated() {
        correctComponentIsUpdated(CLIENT_ID)
        assert(JlsForceState.instance.clientId == CLIENT_ID)
    }

    fun testPasswordIsUpdated() {
        JlsForceState.instance.username = "username"
        correctComponentIsUpdated(PASSWORD)
        assert(JlsForceSecureState.instance.password == PASSWORD)
    }

    fun testClientSecretIsUpdated() {
        JlsForceState.instance.clientId = "clientId"
        correctComponentIsUpdated(CLIENT_SECRET)
        assert(JlsForceSecureState.instance.clientSecret == CLIENT_SECRET)
    }

    fun testBaserURlIsUpdated() {
        correctComponentIsUpdated(BASE_URL)
        assert(JlsForceState.instance.baseUrl == BASE_URL)
    }

    fun testPackageName() {
        correctComponentIsUpdated(PACKAGE_NAME)
        assert(JlsForceState.instance.packageName == PACKAGE_NAME)
    }

    fun testClassPath() {
        correctComponentIsUpdated(CLASS_PATH)
        assert(JlsForceState.instance.classPath == CLASS_PATH)
    }

    fun testLayoutable() {
        JlsForceState.instance.useClassFilters = true
        JlsForceState.instance.useClassList = false
        correctComponentIsUpdated(LAYOUTABLE, "false")
        assert(!JlsForceState.instance.filterLayoutable)
    }

    fun testCreatable() {
        JlsForceState.instance.useClassFilters = true
        JlsForceState.instance.useClassList = false
        correctComponentIsUpdated(CREATABLE, "false")
        assert(!JlsForceState.instance.filterCreatable)
    }

    fun testInterfaces() {
        JlsForceState.instance.useClassFilters = true
        JlsForceState.instance.useClassList = false
        correctComponentIsUpdated(INTERFACES, "true")
        assert(JlsForceState.instance.filterInterfaces)
    }

    fun testClassTextfield() {
        correctComponentIsUpdated(CLASS_LIST)
        assert(JlsForceState.instance.classList == CLASS_LIST)
    }

    fun testUseClassList() {
        correctComponentIsUpdated(USE_CLASS_LIST, "true")
        assert(JlsForceState.instance.useClassList)
        assert(!JlsForceState.instance.useClassFilters)
    }

    fun testSelectUseClassFilters() {
        correctComponentIsUpdated(USE_CLASS_FILTERS, "true")
        assert(JlsForceState.instance.useClassFilters)
        assert(!JlsForceState.instance.useClassList)
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
