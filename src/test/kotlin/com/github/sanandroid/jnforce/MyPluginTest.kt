//
// package com.github.sanandroid.jnforce
//
// import com.github.sanandroid.jnforce.state.JlsForceState
// import com.github.sanandroid.jnforce.settings.JlsForceConfigurable
// import com.intellij.testFramework.TestDataPath
// import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase
//
// @TestDataPath("\$CONTENT_ROOT/src/test/testData")
// class MyPluginTest : LightJavaCodeInsightFixtureTestCase() {
//
//     fun testProjectService() {
//         val jlsForceState = JlsForceState.instance
//         val jlsForceConfigurable = JlsForceConfigurable()
//         val panel = jlsForceConfigurable.createComponent()
//         val jlsForceComponent = jlsForceConfigurable.mySettingsComponent
//         jlsForceComponent!!.userNameText = "user id"
//         jlsForceConfigurable.apply()
//         assert(jlsForceState.userId == jlsForceComponent.userNameText)
//     }
// }
