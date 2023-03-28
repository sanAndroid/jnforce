package com.github.sanandroid.jaleson.services

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.sanandroid.jaleson.dataclassdsl.Salizer
import com.github.sanandroid.jaleson.model.SalesforceCredentials
import com.github.sanandroid.jaleson.settings.JlsForceState
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressIndicatorProvider
import com.intellij.openapi.project.ProjectManager
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

class SalesforceService(private val packagePath: String) : Runnable {

    // Maybe I shouldn't use a singleton here, because it might cause problems with intellij
    private val objectMapper = ObjectMapper()
    private val client = HttpClient.newHttpClient()
    private var token: String = ""
    private val sobjectSuffix = "data/v52.0/sobjects/"
    private val configurationState
        get() = JlsForceState.instance

    // TODO I need to make sure that the project is the one the user is currently working on
    private val salesforceCredentials = getCredentials()

    override fun run() = getSObjectsMetadata()

    fun getSObjectsMetadata() {
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

    fun JsonObject.getFilterFlag(filter: Boolean, name: String, invert: Boolean = false) =
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
        val dataClass = Salizer.dataClassFromJsonForJackson(
            responseAsString,
            packagePath.split("kotlin/").last().replace("/", ".").removeSurrounding(".") + "salesforce",
        )
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
        val newToken = objectMapper.readTree(response.body()).get("access_token").asText()
            ?: throw RuntimeException("Could not get token from response") // Todo Error Handling
        token = newToken
        return newToken
    }

    private fun getProject() = ProjectManager.getInstance().openProjects.first()

    // TODO Store the credentials in a more secure format later on
    private fun getCredentials(): SalesforceCredentials =
        SalesforceCredentials(
            instanceUrl = configurationState.baseUrl,
            clientId = configurationState.clientId,
            clientSecret = configurationState.clientSecret ?: "",
            username = configurationState.userId,
            password = configurationState.password ?: "",
        )
    // val basePath = getProject().basePath
    // val credentials = File("$basePath/salesforce-credentials.json").readText()
    // return decodeFromString(SalesforceCredentials.serializer(), credentials)
    // }

    private fun writeFileDirectlyAsText(path: String = "", fileName: String, fileContent: String) =
        File("$path$fileName").writeText(fileContent, Charsets.UTF_8)

    private fun readFileDirectlyAsText(fileName: String) =
        File("src/test/resources/$fileName").readText(Charsets.UTF_8)
}
