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

    @Nullable
    fun getState() = this

    private val jlsForceState = JlsForceState.instance

    var password: String?
        get() = getter(jlsForceState.username)
        set(newPassword) = setter(jlsForceState.username,newPassword)


    var clientSecret: String?
        get() = getter(jlsForceState.clientId)
        set(newClientSecret) = setter(jlsForceState.clientId, newClientSecret)

    private fun getter(key: String): String? {
        val credentialAttributes = createCredentialAttributes(key)
        val safe = PasswordSafe.instance
        val credential = safe.get(credentialAttributes)
        return credential?.password?.toString()
    }

    private fun setter(key: String, secret: String?) {
        val credentialAttributes = createCredentialAttributes(key)
        val safe = PasswordSafe.instance
        val credential = Credentials(jlsForceState.clientId, secret)
        safe.set(credentialAttributes, credential)
    }

    private fun createCredentialAttributes(key: String): CredentialAttributes {
        val serviceName = generateServiceName("com.github.sanandroid.jlsforce.settings.JForceState", key)
        return CredentialAttributes(serviceName, key)
    }

    companion object {
        val instance: JlsForceSecureState
            get() = ApplicationManager.getApplication().getService(JlsForceSecureState::class.java)
    }
}
