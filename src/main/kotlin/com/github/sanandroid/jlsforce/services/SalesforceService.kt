package com.github.sanandroid.jlsforce.services

import ADDRESS_TEMPLATE
import LOCATION_TEMPLATE
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.sanandroid.jlsforce.dataclassdsl.Salizer
import com.github.sanandroid.jlsforce.helpers.writeFileDirectlyAsText
import com.github.sanandroid.jlsforce.settings.CREATEABLE
import com.github.sanandroid.jlsforce.settings.CUSTOM
import com.github.sanandroid.jlsforce.settings.DELETABLE
import com.github.sanandroid.jlsforce.settings.LAYOUTABLE
import com.github.sanandroid.jlsforce.settings.MERGEABLE
import com.github.sanandroid.jlsforce.settings.REPLICATEABLE
import com.github.sanandroid.jlsforce.settings.RETRIEVEABLE
import com.github.sanandroid.jlsforce.settings.SEARCHABLE
import com.github.sanandroid.jlsforce.settings.UPDATEABLE
import com.github.sanandroid.jlsforce.state.JlsForceSecureState
import com.github.sanandroid.jlsforce.state.JlsForceState
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressIndicatorProvider
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectRootManager
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonObject
import java.io.File
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

const val SOBJECT_SUFFIX = "data/v52.0/sobjects/"

@Service(Service.Level.PROJECT)
class SalesforceService(
    private val project: Project,
) : Runnable {

    val fileSeparator = File.separator ?: "/"

    companion object {
        fun instance(project: Project): SalesforceService = project.service()
        private val client: HttpClient = HttpClient.newHttpClient()
        private val objectMapperWrapper = ObjectMapper()
        var token = ""
        private val logger = logger<SalesforceService>()
    }

    override fun run() {
        val (jlsForceState, jlsForceSecureState, packagePath) = getJlsForceState()
        File(packagePath).mkdirs()
        copyAddressAndLocation(jlsForceState, packagePath)
        val progressIndicator: ProgressIndicator = ProgressIndicatorProvider.getInstance().progressIndicator.apply {
            text = "Getting salesforce objects"
        }
        token = getToken(jlsForceState, jlsForceSecureState)
        if (jlsForceState.useClassFilters) {
            importSObjectsByFilter(progressIndicator, jlsForceState, packagePath, token)
        }
        importSObjectsByList(progressIndicator, jlsForceState, packagePath, token)
    }

    private fun copyAddressAndLocation(jlsForceState: JlsForceState, packagePath: String) {
        val packageDirective = "package ${getPackageName(jlsForceState, packagePath)}"
        val address = packageDirective + ADDRESS_TEMPLATE
        val location = packageDirective + LOCATION_TEMPLATE
        writeFileDirectlyAsText(path = packagePath, "Address.kt", address)
        writeFileDirectlyAsText(path = packagePath, "Location.kt", location)
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
        val fields = getListOfSObjects(jlsForceState, token) ?: return
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
        sObject.getFilterFlag(filter = jlsForceState.filterCustom, name = CUSTOM) &&
            sObject.getFilterFlag(filter = jlsForceState.filterCreatable, name = CREATEABLE) &&
            sObject.getFilterFlag(filter = jlsForceState.filterDeletable, name = DELETABLE) &&
            sObject.getFilterFlag(filter = jlsForceState.filterMergeable, name = MERGEABLE) &&
            sObject.getFilterFlag(filter = jlsForceState.filterLayoutable, name = LAYOUTABLE) &&
            sObject.getFilterFlag(filter = jlsForceState.filterReplicateable, name = REPLICATEABLE) &&
            sObject.getFilterFlag(filter = jlsForceState.filterRetrieveable, name = RETRIEVEABLE) &&
            sObject.getFilterFlag(filter = jlsForceState.filterSearchable, name = SEARCHABLE) &&
            sObject.getFilterFlag(filter = jlsForceState.filterUpdateable, name = UPDATEABLE)

    private fun getListOfSObjects(jlsForceState: JlsForceState, token: String): JsonArray? {
        val objectListUrl = "${jlsForceState.baseUrl}/services/$SOBJECT_SUFFIX".useFS()
        val requestBuilder = HttpRequest.newBuilder()
            .uri(URI.create(objectListUrl))
            .header("Authorization", "Bearer $token")
            .GET()

        val salesforceResponse = makeApiRequest(
            requestBuilder = requestBuilder,
            onSuccess = { response ->
                SalesforceResponse.Success(
                    Json.parseToJsonElement(response.body()).jsonObject.toMap()
                )
            },
        )

        return if (salesforceResponse is SalesforceResponse.Success<Map<String, JsonElement>>) {
            val jsonMap = salesforceResponse.responseBodyAsString
            (jsonMap["sobjects"] as JsonArray)
        } else null
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
        val packageName = getPackageName(jlsForceState, packagePath)
        val requestBuilder = HttpRequest.newBuilder()
            .uri(URI.create("${jlsForceState.baseUrl}/services/$SOBJECT_SUFFIX$sObject/describe".useFS()))
            .header("Authorization", "Bearer $token")
            .GET()
        // val responseAsString = client.send(request, HttpResponse.BodyHandlers.ofString()).body()
        val salesforceResponse = makeApiRequest(
            requestBuilder = requestBuilder,
            onSuccess = { responseAsString ->
                SalesforceResponse.Success(Salizer().dataClassFromJsonForJackson(responseAsString.body(), packageName))
            },
        )
        if (salesforceResponse is SalesforceResponse.Success)
            writeFileDirectlyAsText(
                path = packagePath,
                fileName = "$sObject.kt",
                fileContent = salesforceResponse.responseBodyAsString
            )
    }

    private fun getPackageName(
        jlsForceState: JlsForceState,
        packagePath: String
    ) = (
        if (jlsForceState.packageName.isNullOrEmpty()) {
            packagePath.split("kotlin$fileSeparator").last().replace(fileSeparator, ".").removeSuffix(".")
                .removePrefix(".")
        } else {
            jlsForceState.packageName!!.removeSuffix(".").removePrefix(".")
        }
    ).let { "$it\n" }

    private fun getToken(jlsForceState: JlsForceState, jlsForceSecureState: JlsForceSecureState): String {
        val request = getTokenRequestBuilder(jlsForceState, jlsForceSecureState)

        val salesforceResponse = makeApiRequest(request,
            { response ->
                SalesforceResponse
                    .Success(objectMapperWrapper.readTree(response.body()).get("access_token").asText())
            }
        )
        if (salesforceResponse is SalesforceResponse.Success) {
            return salesforceResponse.responseBodyAsString.toString()
        } else {
            throw Exception("Could not get token from Salesforce")
        }
    }

    private fun getTokenRequest(
        jlsForceState: JlsForceState,
        jlsForceSecureState: JlsForceSecureState
    ): HttpRequest.Builder = HttpRequest.newBuilder()
        .uri(URI.create("${jlsForceState.baseUrl}/services/oauth2/token".useFS()))
        .header("Content-Type", "application/x-www-form-urlencoded")
        .POST(
            HttpRequest.BodyPublishers.ofString(
                "grant_type=password&client_id=${jlsForceState.clientId}" +
                    "&client_secret=${jlsForceSecureState.clientSecret}&" +
                    "username=${jlsForceState.username}&" +
                    "password=${jlsForceSecureState.password}${jlsForceSecureState.securityToken}",
            ),
        )

    private fun <T : Any> makeApiRequest(
        requestBuilder: HttpRequest.Builder,
        onSuccess: (HttpResponse<String>) -> SalesforceResponse.Success<T>,
        onError: (HttpResponse<String>) -> SalesforceResponse.Error<T> = defaultErrorHandler(),
    ): SalesforceResponse<T> {
        client.send(requestBuilder.build(), HttpResponse.BodyHandlers.ofString()).let { response ->
            if (response.statusCode() in 200..299)
                return onSuccess(response)
            if (response.statusCode() == 401) {
                return retryWithNewToken(onSuccess, onError, requestBuilder)
            }
            return onError(response)
        }
    }

    private fun <T : Any> retryWithNewToken(
        onSuccess: (HttpResponse<String>) -> SalesforceResponse.Success<T>,
        onError: (HttpResponse<String>) -> SalesforceResponse.Error<T>,
        requestBuilder: HttpRequest.Builder
    ): SalesforceResponse<T> {
        val tokenRequest = getTokenRequestBuilder(getJlsForceState().first, getJlsForceState().second)
        client.send(tokenRequest.build(), HttpResponse.BodyHandlers.ofString()).let { tokenResponse ->
            if (tokenResponse.statusCode() !in 200..299)
                return onError(tokenResponse)
            token = objectMapperWrapper.readTree(tokenResponse.body()).get("access_token").asText()

        }
        val request = requestBuilder.setHeader("Authorization", "Bearer $token").build()
        val secondResponse = client.send(request, HttpResponse.BodyHandlers.ofString())
        if (secondResponse.statusCode() in 200..299)
            return onSuccess(secondResponse)
        return onError(secondResponse)
    }

    private fun getTokenRequestBuilder(
        jlsForceState: JlsForceState,
        jlsForceSecureState: JlsForceSecureState
    ) = getTokenRequest(jlsForceState, jlsForceSecureState)

    private fun <T> defaultErrorHandler() = { response: HttpResponse<String> ->
        val content = "Error communication with Salesforce: ${response.statusCode()}\n${response.body()}"
        NotificationGroupManager.getInstance()
            //.getNotificationGroup("Error communication with Salesforce")
            .registeredNotificationGroups
            .first()
            .createNotification(content, NotificationType.ERROR)
            .notify(project)
        logger.error(content)
        SalesforceResponse.Error<T>()
    }

    private fun getJlsForceState() =
        JlsForceState.instance.let { jlsForceState ->
            Triple(
                jlsForceState,
                JlsForceSecureState.instance,
                if (jlsForceState.classPath.isNullOrEmpty()) {
                    ProjectRootManager.getInstance(project).contentSourceRoots[0].canonicalPath!! + "/salesforce/".useFS()
                } else {
                    "${project.basePath}/src/main/kotlin/${jlsForceState.classPath!!}/"
                        .replace("//", "/")
                        .replace("\\\\", "\\")
                        .replace("\\s".toRegex(), "")
                        .useFS()
                },
            )
        }

    private fun String.useFS() = replace("/", fileSeparator)
}

sealed class SalesforceResponse<T> {
    class Success<T>(val responseBodyAsString: T) : SalesforceResponse<T>()
    class Error<T> : SalesforceResponse<T>()
}
