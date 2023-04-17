package com.github.sanandroid.jlsforce.services

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.sanandroid.jlsforce.dataclassdsl.Salizer
import com.github.sanandroid.jlsforce.helpers.writeFileDirectlyAsText
import com.github.sanandroid.jlsforce.model.SalesforceCredentials
import com.github.sanandroid.jlsforce.state.JlsForceSecureState
import com.github.sanandroid.jlsforce.state.JlsForceState
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressIndicatorProvider
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectRootManager
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonObject
import java.io.File
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

/**
 * Runs now as Service - does that make sense? This will probably prevent the service from being instantiated
 * multiple times -> That's probably not what I want?
 * TODO() Split this into two classes - one for network requests and one file writer
 *
 */
@Service(Service.Level.PROJECT)
class SalesforceService(
    private val project: Project,
) : Runnable {

    companion object {
        fun instance(project: Project): SalesforceService = project.service()

        // Maybe wrap this in a sep class? -> But that doesn't matter that much since
        // it's a singleton anyway and the service won't be instantiated multiple times
        private val client: HttpClient = HttpClient.newHttpClient()
    }

    private var token: String = ""
    private val sobjectSuffix = "data/v52.0/sobjects/"

    private val objectMapperWrapper = ObjectMapper()

    private var configurationState = JlsForceState.instance
    private var secureConfigurationState = JlsForceSecureState.instance

    private val packagePath: String
        get() =
            if (configurationState.classPath.isNullOrEmpty()) {
                ProjectRootManager.getInstance(project).contentSourceRoots[0].canonicalPath!! + "/salesforce/"
            } else {
                configurationState.classPath!!
            }

    // TODO I need to make sure that the project is the one the user is currently working on
    private val salesforceCredentials = getCredentials()

    override fun run() {
        updateState()
        getSObjectsMetadata()
    }

    private fun getSObjectsMetadata() {
        val progressIndicator: ProgressIndicator = ProgressIndicatorProvider.getInstance().progressIndicator.apply {
            text = "Getting salesforce objects"
        }
        val token = getToken()
        val request = HttpRequest.newBuilder()
            .uri(URI.create("${salesforceCredentials.instanceUrl}/services/$sobjectSuffix"))
            .header("Authorization", "Bearer $token")
            .GET()
            .build()
        val responseAsString = client.send(request, HttpResponse.BodyHandlers.ofString()).body()
        val jsonMap = Json.parseToJsonElement(responseAsString).jsonObject.toMap()
        val fields = (jsonMap["sobjects"] as JsonArray)
        val numberOfFields = fields.size
        File(packagePath).mkdirs()

        fields.forEachIndexed { index, sObject ->
            if (progressIndicator.isCanceled) return
            sObject as JsonObject
            val className = (sObject["name"] as JsonPrimitive).content

            if (
                sObject.getFilterFlag(filter = configurationState.filterLayoutable, name = "layoutable") &&
                sObject.getFilterFlag(filter = configurationState.filterInterfaces, name = "interface") &&
                sObject.getFilterFlag(filter = configurationState.filterCreatable, name = "createable")
            ) {
                createDataclass(className)
            }
            progressIndicator.fraction = index.toDouble() / numberOfFields
            progressIndicator.text2 = "$index of $numberOfFields"
        }
    }

    private fun JsonObject.getFilterFlag(filter: Boolean, name: String, invert: Boolean = false) =
        if (!filter) {
            true
        } else if (invert) {
            !this[name].toString().toBoolean()
        } else {
            this[name].toString().toBoolean()
        }

    private fun createDataclass(sObject: String) {
        val request = HttpRequest.newBuilder()
            .uri(URI.create("${salesforceCredentials.instanceUrl}/services/$sobjectSuffix$sObject/describe"))
            .header("Authorization", "Bearer ${getToken()}")
            .GET()
            .build()
        val responseAsString = client.send(request, HttpResponse.BodyHandlers.ofString()).body()
        val packageName = if (configurationState.packageName.isNullOrEmpty()) {
            packagePath.split("kotlin/").last().replace("/", ".").removeSurrounding(".")
        } else {
            configurationState.packageName!!
        }
        val dataClass = Salizer().dataClassFromJsonForJackson(responseAsString, packageName)
        writeFileDirectlyAsText(path = packagePath, fileName = "$sObject.kt", fileContent = dataClass)
    }

    /**
     * TODO Now gets called for every request, but better to used cached token
     */
    private fun getToken(): String {
        val request = HttpRequest.newBuilder()
            .uri(URI.create("${salesforceCredentials.instanceUrl}/services/oauth2/token"))
            .header("Content-Type", "application/x-www-form-urlencoded")
            .POST(
                HttpRequest.BodyPublishers.ofString(
                    "grant_type=password&client_id=${salesforceCredentials.clientId}" +
                        "&client_secret=${salesforceCredentials.clientSecret}&" +
                        "username=${salesforceCredentials.username}&" +
                        "password=${salesforceCredentials.password}",
                ),
            )
            .build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
        val newToken = objectMapperWrapper.readTree(response.body()).get("access_token").asText()
            ?: throw RuntimeException("Could not get token from response") // Todo Error Handling
        token = newToken
        return newToken
    }

    private fun updateState() {
        configurationState = JlsForceState.instance
        secureConfigurationState = JlsForceSecureState.instance
    }

    private fun getCredentials(): SalesforceCredentials =
        SalesforceCredentials(
            instanceUrl = configurationState.baseUrl,
            clientId = configurationState.clientId,
            clientSecret = secureConfigurationState.clientSecret ?: "",
            username = configurationState.username,
            password = secureConfigurationState.password ?: "",
        )
}
