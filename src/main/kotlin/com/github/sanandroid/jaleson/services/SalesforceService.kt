package com.github.sanandroid.jaleson.services

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.sanandroid.jaleson.dataclassdsl.Salizer
import com.github.sanandroid.jaleson.model.SalesforceCredentials
import com.intellij.openapi.project.ProjectManager
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.Json.Default.decodeFromString
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonObject
import java.io.File
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

class SalesforceService : Runnable {

    // Maybe I shouldn't use a singleton here, because it might cause problems with intellij
    private val objectMapper = ObjectMapper()
    private val client = HttpClient.newHttpClient()
    private var token: String = ""
    private val sobjectSuffix = "data/v52.0/sobjects/"

    // TODO I need to make sure that the project is the one the user is currently working on
    private val projectPath = ProjectManager.getInstance().openProjects[0].basePath
    private val salesforceCredentials = getCredentials()

    override fun run() = getSObjectsMetadata()

    /**
     * TODO Now gets called for every request, but better to used cached token
     */
    fun getToken(): String {
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

    fun getSObjectsMetadata() {
        val token = getToken()
        val request = HttpRequest.newBuilder()
            .uri(URI.create("${salesforceCredentials.instanceUrl}/services/data/v50.0/sobjects"))
            .header("Authorization", "Bearer $token")
            .GET()
            .build()
        val responseAsString = client.send(request, HttpResponse.BodyHandlers.ofString()).body()
        val jsonMap = Json.parseToJsonElement(responseAsString).jsonObject.toMap()
        val fields = (jsonMap["sobjects"] as JsonArray)
        fields.forEach { sObject ->
            sObject as JsonObject
            val className = (sObject["name"] as JsonPrimitive).content
            createDataclass(className)
        }
    }

    private fun createDataclass(sObject: String) {
        val request = HttpRequest.newBuilder()
            .uri(URI.create("${salesforceCredentials.instanceUrl}/services/data/v50.0/sobjects/$sObject/describe"))
            .header("Authorization", "Bearer ${getToken()}")
            .GET()
            .build()
        val responseAsString = client.send(request, HttpResponse.BodyHandlers.ofString()).body()
        val dataClass = Salizer.dataClassFromJson(responseAsString)
        writeFileDirectlyAsText(fileName = "$sObject.kt", fileContent = dataClass)
    }

    private fun getProject() = ProjectManager.getInstance().openProjects.first()

    // TODO Store the credentials in a more secure format later on
    private fun getCredentials(): SalesforceCredentials {
        val basePath = getProject().basePath
        val credentials = File("$basePath/salesforce-credentials.json").readText()
        return decodeFromString(SalesforceCredentials.serializer(), credentials)
    }

    private fun writeFileDirectlyAsText(path: String = "", fileName: String, fileContent: String) =
        File("$projectPath/$path$fileName").writeText(fileContent, Charsets.UTF_8)

    private fun readFileDirectlyAsText(fileName: String) =
        File("src/test/resources/$fileName").readText(Charsets.UTF_8)
}
