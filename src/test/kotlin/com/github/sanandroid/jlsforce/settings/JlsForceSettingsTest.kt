package com.github.sanandroid.jlsforce.settings

import com.github.sanandroid.jlsforce.state.JlsForceSecureState
import com.github.sanandroid.jlsforce.state.JlsForceState
import com.intellij.testFramework.TestDataPath
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBTextField
import javax.swing.JPasswordField

@TestDataPath("\$CONTENT_ROOT/src/test/testData")
class JlsForceSettingsTest : LightJavaCodeInsightFixtureTestCase() {

    fun testUserNameIsUpdated() {
        correctComponentIsUpdated(USERNAME)
        assert(JlsForceState.instance.username == USERNAME)
    }

    fun testPasswordIsUpdated() {
        correctComponentIsUpdated(PASSWORD)
        assert(JlsForceSecureState.instance.password == PASSWORD)
    }

    fun testClientIdIsUpdated() {
        correctComponentIsUpdated(CLIENT_ID)
        assert(JlsForceState.instance.clientId == CLIENT_ID)
    }

    fun testClientSecretIsUpdated() {
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
        correctComponentIsUpdated(LAYOUTABLE, "false")
        assert(!JlsForceState.instance.filterLayoutable)
    }

    fun testCreatable() {
        correctComponentIsUpdated(CREATABLE, "false")
        assert(!JlsForceState.instance.filterCreatable)
    }

    fun testInterfaces() {
        correctComponentIsUpdated(INTERFACES, "true")
        assert(JlsForceState.instance.filterInterfaces)
    }

    private fun correctComponentIsUpdated(
        componentName: String,
        setTo: String = componentName,
    ) {
        val jlsForceConfigurable = JlsForceConfigurable()
        val panel = jlsForceConfigurable.createComponent()
        when (val jComponent = (panel?.components?.find { it.name == componentName }!!)) {
            is JBCheckBox -> jComponent.isSelected = setTo.toBoolean()
            is JBTextField -> jComponent.text = setTo
            is JPasswordField -> jComponent.text = setTo
        }
        jlsForceConfigurable.apply()
    }
}
