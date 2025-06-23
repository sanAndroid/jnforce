package com.github.sanandroid.jnforce.dataclassdsl

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonObject
import com.github.sanandroid.jnforce.model.toKotlinType

class Salizer {

    fun dataClassFromJsonForJackson(jsonString: String, packageName: String): String {
        val jsonMap = Json.parseToJsonElement(jsonString).jsonObject.toMap()
        // val describe = (jsonMap["objectDescribe"] as Map<String, JsonElement>)
        val className = (jsonMap["name"] as JsonPrimitive).content.qualify()

        val dataClass = dataClass {
            packageName { packageName }

            imports {
                // Default imports for jackson
                import { "com.fasterxml.jackson.annotation.JsonIgnoreProperties" }
                import { "com.fasterxml.jackson.annotation.JsonInclude" }
                import { "com.fasterxml.jackson.annotation.JsonProperty" }

                // Salesforce Types Imports
                import { "me.campos.corp.jnforce.model.Address" }
                import { "me.campos.corp.jnforce.model.Location" }
            }
            // Right now this automatically added { modifier { "data" } }
            name { className }
            annotations {
                annotation { "@JsonInclude(JsonInclude.Include.NON_NULL)" }
                annotation { "@JsonIgnoreProperties(ignoreUnknown = true)" }
            }
            parameters { buildParameters(jsonMap["fields"] as JsonArray) }
        }.build()
        return dataClass
    }

    private fun ParametersBuilder.buildParameters(fields: JsonArray) {

        fields.forEach { field ->
            @Suppress("UNCHECKED_CAST")
            field as Map<String, JsonElement>
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

    fun String.qualify(): String = this.replace("__c", "").replace("__s", "").replace("_", "")
}
