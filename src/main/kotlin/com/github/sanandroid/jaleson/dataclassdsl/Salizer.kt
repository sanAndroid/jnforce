package com.github.sanandroid.jaleson.dataclassdsl

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonObject
import me.campos.corp.jaleson.model.SalesforceType
import me.campos.corp.jaleson.model.toKotlinType
import me.campos.corp.jaleson.poet.dataClass

object Salizer {

    fun dataClassFromJson(jsonString: String, packageName: String = "poet"): String {
        val jsonMap = Json.parseToJsonElement(jsonString).jsonObject.toMap()
        val fields = (jsonMap["fields"] as JsonArray)
        val className = (jsonMap["name"] as JsonPrimitive).content

        val dataClass = dataClass {
            packageName { packageName }

            imports {
                // Default imports for jackson
                import { "com.fasterxml.jackson.annotation.JsonIgnoreProperties" }
                import { "com.fasterxml.jackson.annotation.JsonInclude" }
                import { "com.fasterxml.jackson.annotation.JsonProperty" }

                // Salesforce Types Import
                import { "me.campos.corp.jaleson.model.Address" }
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
            val variableName = jsonName.replaceFirstChar { char -> char.lowercase() }.replace("__c", "")
            val type =
                SalesforceType.valueOf((field["type"] as JsonPrimitive).content.uppercase()).toKotlinType()
            parameter {
                val optional = (field["nillable"] as JsonPrimitive).content.toBoolean()
                annotations { annotation { "@JsonProperty(\"$jsonName\")" } }
                mutable { if ((field["updateable"] as JsonPrimitive).content.toBoolean()) Mutable.VAR else Mutable.VAL }
                name { jsonName }
                optional { optional }
                type { type }
                if (optional) {
                    defaultValue { "null" }
                }
            }
        }
    }
}
