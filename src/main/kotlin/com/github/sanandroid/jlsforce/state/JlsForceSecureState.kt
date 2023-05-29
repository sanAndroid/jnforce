package com.github.sanandroid.jlsforce.state

import com.intellij.credentialStore.CredentialAttributes
import com.intellij.credentialStore.Credentials
import com.intellij.credentialStore.generateServiceName
import com.intellij.ide.passwordSafe.PasswordSafe
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import org.jetbrains.annotations.Nullable

/**
 * Supports storing the application settings in a persistent way.
 * The [State] and [Storage] annotations define the name of the data and the file name where
 * these persistent application settings are stored.
 */
@Service(Service.Level.PROJECT)
class JlsForceSecureState {

    val safe = PasswordSafe.instance

    @Nullable
    fun getState() = this

    private val jlsForceState = JlsForceState.instance

    var clientSecret: String?
        get() {
            val credentialAttributes = createCredentialAttributes(jlsForceState.clientId)
            val credential = safe.get(credentialAttributes)
            return credential?.password?.toString()
        }
        set(newSecret) {
            val credentialAttributes = createCredentialAttributes(jlsForceState.clientId)
            val credential = Credentials(jlsForceState.clientId, newSecret)
            safe.set(credentialAttributes, credential)
        }

    var password: String?
        get() {
            val credentialAttributes = createCredentialAttributes(jlsForceState.username)
            val credential = safe.get(credentialAttributes)
            return credential?.password?.toString()
        }
        set(newPassword) {
            val credentialAttributes = createCredentialAttributes(jlsForceState.username)
            val credential = Credentials(jlsForceState.username, newPassword)
            safe.set(credentialAttributes, credential)
        }
    var securityToken: String?
        get() {
            val credentialAttributes = createCredentialAttributes(jlsForceState.username + "_token")
            val credential = safe.get(credentialAttributes)
            return credential?.password?.toString()
        }
        set(newToken) {
            val credentialAttributes = createCredentialAttributes(jlsForceState.username + "_token")
            val credential = Credentials(jlsForceState.username + "_token", newToken)
            safe.set(credentialAttributes, credential)
        }

    private fun createCredentialAttributes(key: String): CredentialAttributes {
        val serviceName = generateServiceName("com.github.sanandroid.jlsforce.settings.JlsForceState", key)
        return CredentialAttributes(serviceName, key)
    }

    companion object {
        val instance: JlsForceSecureState
            get() = ApplicationManager.getApplication().getService(JlsForceSecureState::class.java)
    }
}
