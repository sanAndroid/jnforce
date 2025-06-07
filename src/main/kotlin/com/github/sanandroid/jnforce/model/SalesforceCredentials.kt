package com.github.sanandroid.jnforce.model

// TODO: Probably needs to be serializable again

data class SalesforceCredentials(
    val instanceUrl: String,
    // val username: String,
    // val password: String,
    // val securityToken: String,
    val clientId: String,
    val clientSecret: String,
)
