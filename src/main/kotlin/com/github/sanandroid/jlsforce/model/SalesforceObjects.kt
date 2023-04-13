package me.campos.corp.jlsforce.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.intellij.openapi.diagnostic.thisLogger

data class SObjectMetaDataDto(
    @JsonProperty("fields")
    val fields: List<FieldDto>,
)

data class SObjectRecordsDto(
    @JsonProperty("records")
    val records: List<SObjectRecordDto>,
)

data class SObjectRecordDto(
    @JsonProperty("Id")
    val id: String,
    @JsonProperty("Name")
    val name: String,
)

abstract class AbtractSalesforceError(
    open val message: String,
    open val errorCode: String,
)

data class SalesforceMetadataError(
    override val message: String = "No Error Message provided",
    override val errorCode: String = "No Error Code provided",
    val fields: List<String> = listOf(),
) : AbtractSalesforceError(message, errorCode)

data class SalesforceRecordsError(
    override val message: String = "No Error Message provided",
    override val errorCode: String = "No Error Code provided",
    val records: List<SObjectRecordDto> = listOf(),
) : AbtractSalesforceError(message, errorCode)

data class FieldDto(
    val label: String?,
    val name: String,
    val createable: Boolean?,
    val type: SalesforceType,
    val nillable: Boolean,
)

/**
 * Where did I get this list? It would be nice to populate that via api.
 * Also Note that Salesforce Types and Objects are different. For example
 * There is a Location Object and a Location Type.
 */

enum class SalesforceType {
    ID,
    ADDRESS,
    ANYTYPE,
    BASE64,
    BOOLEAN,
    CALCULATED,
    COMPLEXVALUE,
    DATACATEGORYGROUPREFERENCE,
    CURRENCY,
    DATE,
    DATETIME,
    DOUBLE,
    EMAIL,
    ENCRYPTEDSTRING,
    INT,
    JSON,
    JUNCTIONIDLIST,
    LONG,
    LOCATION,
    MASTERRECORD,
    MULTIPICKLIST,
    PERCENT,
    PHONE,
    PICKLIST,
    COMBOBOX,
    REFERENCE,
    TEXTAREA,
    TIME,
    STRING,
    URL,
    UNKOWN,
}

fun String.savelyToSalesforceType() = kotlin.runCatching { SalesforceType.valueOf(this) }.getOrElse {
    thisLogger().warn("Could not map $this to SalesforceType. Using SalesforceType.UNKOWN")
    SalesforceType.UNKOWN
}

fun String.toKotlinType() = when (savelyToSalesforceType()) {
    SalesforceType.BOOLEAN -> MappedType.BOOLEAN
    SalesforceType.DOUBLE -> MappedType.DOUBLE
    SalesforceType.INT -> MappedType.INT
    SalesforceType.ADDRESS -> MappedType.ADDRESS
    else -> MappedType.STRING
}
