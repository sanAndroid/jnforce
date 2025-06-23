package com.github.sanandroid.jnforce.model

const val ADDRESS_TEMPLATE ="""import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
data class Address(

    @JsonProperty("city")
    var city: String? = null,

    @JsonProperty("country")
    var country: String? = null,

    @JsonProperty("countryCode")
    var countryCode: String? = null,

    @JsonProperty("geocodeAccuracy")
    var geocodeAccuracy: String? = null,

    @JsonProperty("postalCode")
    var postalCode: String? = null,

    @JsonProperty("street")
    var street: String? = null,

    @JsonProperty("state")
    var state: String? = null,

    @JsonProperty("stateCode")
    var stateCode: String? = null,

    @JsonProperty("latitude")
    var latitude: String? = null,

    @JsonProperty("longitude")
    var longitude: String? = null,
) """

