package com.github.sanandroid.jnforce.state

import com.intellij.credentialStore.CredentialAttributes
import com.intellij.credentialStore.Credentials
import com.intellij.credentialStore.generateServiceName
import com.intellij.ide.passwordSafe.PasswordSafe
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.Service
import org.jetbrains.annotations.Nullable

@Service(Service.Level.APP)
class JnForceSecureState {

    val safe = PasswordSafe.instance

    @Nullable
    @Suppress("UNUSED")
    fun getState() = this

    private val jnForceState = JnForceState.instance

    var clientSecret: String?
        get() {
            val credentialAttributes = createCredentialAttributes(jnForceState.clientId)
            val credential = safe.get(credentialAttributes)
            return credential?.password?.toString()
        }
        set(newSecret) {
            val credentialAttributes = createCredentialAttributes(jnForceState.clientId)
            val credential = Credentials(jnForceState.clientId, newSecret)
            safe.set(credentialAttributes, credential)
        }

    // var password: String?
    //     get() {
    //         val credentialAttributes = createCredentialAttributes(jlsForceState.username)
    //         val credential = safe.get(credentialAttributes)
    //         return credential?.password?.toString()
    //     }
    //     set(newPassword) {
    //         val credentialAttributes = createCredentialAttributes(jlsForceState.username)
    //         val credential = Credentials(jlsForceState.username, newPassword)
    //         safe.set(credentialAttributes, credential)
    //     }
    // var securityToken: String?
    //     get() {
    //         val credentialAttributes = createCredentialAttributes(jlsForceState.username + "_token")
    //         val credential = safe.get(credentialAttributes)
    //         return credential?.password?.toString()
    //     }
    //     set(newToken) {
    //         val credentialAttributes = createCredentialAttributes(jlsForceState.username + "_token")
    //         val credential = Credentials(jlsForceState.username + "_token", newToken)
    //         safe.set(credentialAttributes, credential)
    //     }

    private fun createCredentialAttributes(key: String): CredentialAttributes {
        val serviceName = generateServiceName("com.github.sanandroid.jnforce.settings.JlsForceState", key)
        return CredentialAttributes(serviceName, key)
    }

    companion object {
        val instance: JnForceSecureState
            get () = ApplicationManager.getApplication().getService(JnForceSecureState::class.java)
    }
}
