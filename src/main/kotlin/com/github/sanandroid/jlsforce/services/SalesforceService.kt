package com.github.sanandroid.jlsforce.services

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.sanandroid.jlsforce.dataclassdsl.Salizer
import com.github.sanandroid.jlsforce.helpers.writeFileDirectlyAsText
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

const val SOBJECT_SUFFIX = "data/v52.0/sobjects/"

@Service(Service.Level.PROJECT)
class SalesforceService(
    private val project: Project,
) : Runnable {

    companion object {
        fun instance(project: Project): SalesforceService = project.service()

        private val client: HttpClient = HttpClient.newHttpClient()
        private val objectMapperWrapper = ObjectMapper()
    }

    override fun run() {
        val (jlsForceState, jlsForceSecureState, packagePath) = getJlsForceState()
        File(packagePath).mkdirs()
        val progressIndicator: ProgressIndicator = ProgressIndicatorProvider.getInstance().progressIndicator.apply {
            text = "Getting salesforce objects"
        }
        val token = getToken(jlsForceState, jlsForceSecureState)
        if (jlsForceState.useClassFilters) {
            importSObjectsByFilter(progressIndicator, jlsForceState, packagePath, token)
        }
        importSObjectsByList(progressIndicator, jlsForceState, packagePath, token)
    }

    private fun importSObjectsByList(
        progressIndicator: ProgressIndicator,
        jlsForceState: JlsForceState,
        packagePath: String,
        token: String,
    ) {
        val objectList = jlsForceState.classList.split(",", ";").map { it.trim() }
        objectList.forEachIndexed { index, className ->
            createDataclass(className, jlsForceState, packagePath, token)
            progressIndicator.fraction = index.toDouble() / objectList.size
            progressIndicator.text2 = "$index of ${objectList.size}"
        }
    }

    private fun importSObjectsByFilter(
        progressIndicator: ProgressIndicator,
        jlsForceState: JlsForceState,
        packagePath: String,
        token: String,
    ) {
        val fields = getListOfSObjects(jlsForceState, token)
        val numberOfFields = fields.size
        fields.forEachIndexed { index, sObject ->
            sObject as JsonObject

            if (progressIndicator.isCanceled) return

            val className = (sObject["name"] as JsonPrimitive).content
            if (evalulateFilters(sObject, jlsForceState)) {
                createDataclass(className, jlsForceState, packagePath, token)
            }

            progressIndicator.fraction = index.toDouble() / numberOfFields
            progressIndicator.text2 = "$index of $numberOfFields"
        }
    }

    private fun evalulateFilters(sObject: JsonObject, jlsForceState: JlsForceState) =
        sObject.getFilterFlag(filter = jlsForceState.filterLayoutable, name = "layoutable") &&
            sObject.getFilterFlag(filter = jlsForceState.filterInterfaces, name = "interface", invert = true) &&
            sObject.getFilterFlag(filter = jlsForceState.filterCreatable, name = "createable")

    private fun getListOfSObjects(jlsForceState: JlsForceState, token: String): JsonArray {
        val request = HttpRequest.newBuilder()
            .uri(URI.create("${jlsForceState.baseUrl}/services/$SOBJECT_SUFFIX"))
            .header("Authorization", "Bearer $token")
            .GET()
            .build()
        val responseAsString = client.send(request, HttpResponse.BodyHandlers.ofString()).body()
        val jsonMap = Json.parseToJsonElement(responseAsString).jsonObject.toMap()
        val fields = (jsonMap["sobjects"] as JsonArray)
        return fields
    }

    private fun JsonObject.getFilterFlag(filter: Boolean, name: String, invert: Boolean = false): Boolean {
        if (!filter) {
            return true
        }
        return if (invert) {
            !this[name].toString().toBoolean()
        } else {
            this[name].toString().toBoolean()
        }
    }

    private fun createDataclass(sObject: String, jlsForceState: JlsForceState, packagePath: String, token: String) {
        val request = HttpRequest.newBuilder()
            .uri(URI.create("${jlsForceState.baseUrl}/services/$SOBJECT_SUFFIX$sObject/describe"))
            .header("Authorization", "Bearer $token")
            .GET()
            .build()
        val responseAsString = client.send(request, HttpResponse.BodyHandlers.ofString()).body()
        val packageName = if (jlsForceState.packageName.isNullOrEmpty()) {
            packagePath.split("kotlin/").last().replace("/", ".").removeSurrounding(".")
        } else {
            jlsForceState.packageName!!
        }
        val dataClass = Salizer().dataClassFromJsonForJackson(responseAsString, packageName)
        writeFileDirectlyAsText(path = packagePath, fileName = "$sObject.kt", fileContent = dataClass)
    }

    private fun getToken(jlsForceState: JlsForceState, jlsForceSecureState: JlsForceSecureState): String {
        val request = HttpRequest.newBuilder()
            .uri(URI.create("${jlsForceState.baseUrl}/services/oauth2/token"))
            .header("Content-Type", "application/x-www-form-urlencoded")
            .POST(
                HttpRequest.BodyPublishers.ofString(
                    "grant_type=password&client_id=${jlsForceState.clientId}" +
                        "&client_secret=${jlsForceSecureState.clientSecret}&" +
                        "username=${jlsForceState.username}&" +
                        "password=${jlsForceSecureState.password}",
                ),
            )
            .build()

        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
        return objectMapperWrapper.readTree(response.body()).get("access_token").asText()
            ?: throw RuntimeException("Could not get token from response")
    }

    private fun getJlsForceState() =
        JlsForceState.instance.let { jlsForceState ->
            Triple(
                jlsForceState,
                JlsForceSecureState.instance,
                if (jlsForceState.classPath.isNullOrEmpty()) {
                    ProjectRootManager.getInstance(project).contentSourceRoots[0].canonicalPath!! + "/salesforce/"
                } else {
                    "${project.basePath}/src/main/kotlin/${jlsForceState.classPath!!}/".replace("//", "/").replace("\\s".toRegex(), "")
                },
            )
        }
}
