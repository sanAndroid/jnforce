package com.github.sanandroid.jnforce.model

const val LOCATION_TEMPLATE = """import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
data class Location(

    @JsonProperty("latitude")
    var latitude: Double? = null,

    @JsonProperty("longitude")
    var longitude: Double? = null,
)"""