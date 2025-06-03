package com.github.sanandroid.jlsforce.dataclassdsl

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonObject
import me.campos.corp.jlsforce.model.toKotlinType
import me.campos.corp.jlsforce.poet.dataClass

class Salizer {

    fun dataClassFromJsonForJackson(jsonString: String, packageName: String): String {
        val jsonMap = Json.parseToJsonElement(jsonString).jsonObject.toMap()
        val fields = (jsonMap["fields"] as JsonArray) //TODO this unsafe
        val className = (jsonMap["name"] as JsonPrimitive).content.qualify()

        val dataClass = dataClass {
            packageName { packageName }

            imports {
                // Default imports for jackson
                import { "com.fasterxml.jackson.annotation.JsonIgnoreProperties" }
                import { "com.fasterxml.jackson.annotation.JsonInclude" }
                import { "com.fasterxml.jackson.annotation.JsonProperty" }

                // Salesforce Types Imports
                import { "me.campos.corp.jlsforce.model.Address" }
                import { "me.campos.corp.jlsforce.model.Location" }
            }
            // Right now this automatically added { modifier { "data" } }
            name { className }
            annotations {
                annotation { "@JsonInclude(JsonInclude.Include.NON_NULL)" }
                annotation { "@JsonIgnoreProperties(ignoreUnknown = true)" }
            }
            parameters { buildParameters(fields) }
        }.build()
        return dataClass
    }

    private fun ParametersBuilder.buildParameters(fields: JsonArray) {
        fields.forEach { field ->
            field as JsonObject

            val jsonName = (field["name"] as JsonPrimitive).content
            val variableName = jsonName.replaceFirstChar { char -> char.lowercase() }.qualify()
            val type = (field["type"] as JsonPrimitive).content.uppercase().toKotlinType()
            val optional = (field["nillable"] as JsonPrimitive).content.toBoolean()
            val mutable = if ((field["updateable"] as JsonPrimitive).content.toBoolean()) Mutable.VAR else Mutable.VAL

            parameter {
                annotations { annotation { "@JsonProperty(\"$jsonName\")" } }
                mutable { mutable }
                name { variableName }
                optional { optional }
                type { type }
                if (optional) {
                    defaultValue { "null" }
                }
            }
        }
    }

    fun String.qualify(): String = replace("__c", "").replace("__s", "").replace("_", "")
}
