package com.github.sanandroid.jnforce.services

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.sanandroid.jnforce.dataclassdsl.Salizer
import com.github.sanandroid.jnforce.helpers.writeFileDirectlyAsText
import com.github.sanandroid.jnforce.model.ADDRESS_TEMPLATE
import com.github.sanandroid.jnforce.model.LOCATION_TEMPLATE
import com.github.sanandroid.jnforce.settings.CREATEABLE
import com.github.sanandroid.jnforce.settings.CUSTOM
import com.github.sanandroid.jnforce.settings.DELETABLE
import com.github.sanandroid.jnforce.settings.LAYOUTABLE
import com.github.sanandroid.jnforce.settings.MERGEABLE
import com.github.sanandroid.jnforce.settings.REPLICATEABLE
import com.github.sanandroid.jnforce.settings.RETRIEVEABLE
import com.github.sanandroid.jnforce.settings.SEARCHABLE
import com.github.sanandroid.jnforce.settings.UPDATEABLE
import com.github.sanandroid.jnforce.state.JnForceSecureState
import com.github.sanandroid.jnforce.state.JnForceState
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.Logger
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
        private val logger = Logger.getInstance(SalesforceService::class.java)
    }

    override fun run() {
        val (jnForceState, jnForceSecureState, packagePath) = getJnForceState()
        File(packagePath).mkdirs()
        copyAddressAndLocation(jnForceState, packagePath)
        val progressIndicator: ProgressIndicator = ProgressIndicatorProvider.getInstance().progressIndicator.apply {
            text = "Getting salesforce objects"
        }
        token = getToken(jnForceState, jnForceSecureState)
        if (jnForceState.useClassFilters) {
            importSObjectsByFilter(progressIndicator, jnForceState, packagePath, token)
        } else {
            importSObjectsByList(progressIndicator, jnForceState, packagePath, token)
        }
    }

    fun testConnection(): Boolean = runCatching {
        val (jnForceState, jnForceSecureState, _) = getJnForceState()
        getToken(jnForceState, jnForceSecureState)
        true
    }.getOrDefault(false)

    private fun copyAddressAndLocation(jnForceState: JnForceState, packagePath: String) {
        val packageDirective = "package ${jnForceState.packageName}"
        val address = packageDirective + ADDRESS_TEMPLATE
        val location = packageDirective + LOCATION_TEMPLATE
        writeFileDirectlyAsText(path = packagePath, "Address.kt", address)
        writeFileDirectlyAsText(path = packagePath, "Location.kt", location)
    }

    private fun importSObjectsByList(
        progressIndicator: ProgressIndicator,
        jnForceState: JnForceState,
        packagePath: String,
        token: String,
    ) {
        val objectList = jnForceState.classList.split(",", ";").map { it.trim() }
        objectList.forEachIndexed { index, className ->
            createDataclass(className, jnForceState, packagePath, token)
            progressIndicator.fraction = index.toDouble() / objectList.size
            progressIndicator.text2 = "$index of ${objectList.size}"
        }
    }

    private fun importSObjectsByFilter(
        progressIndicator: ProgressIndicator,
        jnForceState: JnForceState,
        packagePath: String,
        token: String,
    ) {
        val fields = getListOfSObjects(jnForceState, token) ?: return
        val numberOfFields = fields.size
        fields.forEachIndexed { index, sObject ->
            sObject as JsonObject

            if (progressIndicator.isCanceled) return

            val className = (sObject["name"] as JsonPrimitive).content
            val evaluateFilters = evaluateFilters(sObject, jnForceState)
            if (evaluateFilters) {
                createDataclass(className, jnForceState, packagePath, token)
            }

            progressIndicator.fraction = index.toDouble() / numberOfFields
            progressIndicator.text2 = "$index of $numberOfFields"
        }
    }

    private fun evaluateFilters(sObject: JsonObject, jnForceState: JnForceState) =
        sObject.getFilterFlag(filter = jnForceState.filterCustom, name = CUSTOM) &&
            sObject.getFilterFlag(filter = jnForceState.filterCreatable, name = CREATEABLE) &&
            sObject.getFilterFlag(filter = jnForceState.filterDeletable, name = DELETABLE) &&
            sObject.getFilterFlag(filter = jnForceState.filterMergeable, name = MERGEABLE) &&
            sObject.getFilterFlag(filter = jnForceState.filterLayoutable, name = LAYOUTABLE) &&
            sObject.getFilterFlag(filter = jnForceState.filterReplicateable, name = REPLICATEABLE) &&
            sObject.getFilterFlag(filter = jnForceState.filterRetrieveable, name = RETRIEVEABLE) &&
            sObject.getFilterFlag(filter = jnForceState.filterSearchable, name = SEARCHABLE) &&
            sObject.getFilterFlag(filter = jnForceState.filterUpdateable, name = UPDATEABLE)

    private fun getListOfSObjects(jnForceState: JnForceState, token: String): JsonArray? {
        val objectListUrl = "${jnForceState.baseUrl}/services/$SOBJECT_SUFFIX".useFS()
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

    private fun createDataclass(sObject: String, jnForceState: JnForceState, packagePath: String, token: String) {
        val requestBuilder = HttpRequest.newBuilder()
            .uri(URI.create("${jnForceState.baseUrl}/services/$SOBJECT_SUFFIX$sObject/describe".useFS()))
            .header("Authorization", "Bearer $token")
            .GET()
        // val responseAsString = client.send(request, HttpResponse.BodyHandlers.ofString()).body()
        val salesforceResponse = makeApiRequest(
            requestBuilder = requestBuilder,
            onSuccess = { responseAsString ->
                SalesforceResponse.Success(Salizer().dataClassFromJsonForJackson(responseAsString.body(),
                    jnForceState.packageName
                ))
            },
        )
        if (salesforceResponse is SalesforceResponse.Success)
            writeFileDirectlyAsText(
                path = packagePath,
                fileName = "$sObject.kt",
                fileContent = salesforceResponse.responseBodyAsString
            )
    }

    private fun getToken(jnForceState: JnForceState, jnForceSecureState: JnForceSecureState): String {
        val request = getTokenRequest(jnForceState, jnForceSecureState)

        val salesforceResponse = makeApiRequest(
            request,
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
        jnForceState: JnForceState,
        jnForceSecureState: JnForceSecureState
    ): HttpRequest.Builder = HttpRequest.newBuilder()
        .uri(URI.create("${jnForceState.baseUrl}/services/oauth2/token".useFS()))
        .header("Content-Type", "application/x-www-form-urlencoded")
        .POST(
            HttpRequest.BodyPublishers.ofString(
                "grant_type=client_credentials&client_id=${jnForceState.clientId}" +
                    "&client_secret=${jnForceSecureState.clientSecret}"
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
        val tokenRequest = getTokenRequest(getJnForceState().first, getJnForceState().second)
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

    private fun getJnForceState() = Triple(
        JnForceState.instance,
        JnForceSecureState.instance,
        createClassPath(),
    )

    private fun createClassPath() = JnForceState.instance.packageName.replace(".", fileSeparator)

    private fun String.useFS() = this.replace("/", fileSeparator)
}

sealed class SalesforceResponse<T> {
    class Success<T>(val responseBodyAsString: T) : SalesforceResponse<T>()
    class Error<T> : SalesforceResponse<T>()
}
