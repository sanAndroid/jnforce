package com.github.sanandroid.jlsforce.settings

import com.github.sanandroid.jlsforce.services.JlsForceSecureState
import com.github.sanandroid.jlsforce.services.JlsForceState
import com.intellij.testFramework.TestDataPath
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBTextField
import javax.swing.JPasswordField

@TestDataPath("\$CONTENT_ROOT/src/test/testData")
class JlsForceConfigurableTest : LightJavaCodeInsightFixtureTestCase() {

    fun testUserNameIsUpdated() {
        correctComponentIsUpdated(USERNAME)
        assert(JlsForceState.instance.username == USERNAME)
    }

    fun testPasswordIsUpdated() {
        correctComponentIsUpdated(PASSWORD)
        // assert(JlsForceSecureState.instance.password == PASSWORD)k
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
        correctComponentIsUpdated(CLIENT_ID)
        assert(JlsForceState.instance.clientId == CLIENT_ID)
    }

    fun testPackageName() {
        correctComponentIsUpdated(CLIENT_ID)
        assert(JlsForceState.instance.clientId == CLIENT_ID)
    }

    fun testClassPath() {
        correctComponentIsUpdated(CLIENT_ID)
        assert(JlsForceState.instance.clientId == CLIENT_ID)
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
