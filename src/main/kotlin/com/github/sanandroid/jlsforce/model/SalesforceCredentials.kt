package com.github.sanandroid.jlsforce.model

import kotlinx.serialization.Serializable

/**
 * TODO The security token is already appended to the password -> this should be changed once auth is working
 */
@Serializable
data class SalesforceCredentials(
    val instanceUrl: String,
    val username: String,
    val password: String,
    val securityToken: String,
    val clientId: String,
    val clientSecret: String,
)
