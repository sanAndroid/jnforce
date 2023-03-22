package me.campos.corp.jaleson.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

// Might need to doublecheck this and move to separate file
enum class MappedType(val type: String) {
    STRING("String"),
    INT("Int"),
    LOCALDATE("LocalDate"),
    BOOLEAN("Boolean"),
    DOUBLE("Double"),
    FLOAT("Float"),
    LONG("Long"),
    SHORT("Short"),
    BYTE("Byte"),
    CHAR("Char"),
    BIGDECIMAL("BigDecimal"),
    BIGINTEGER("BigInteger"),
    OBJECT("Object"),
    STRINGARRAY("Array<String>"),
    INTARRAY("Array<Int>"),
    LOCALDATEARRAY("Array<LocalDate>"),
    BOOLEANARRAY("Array<Boolean>"),
    DOUBLEARRAY("Array<Double>"),
    FLOATARRAY("Array<Float>"),
    LONGARRAY("Array<Long>"),
    SHORTARRAY("Array<Short>"),
    BYTEARRAY("Array<Byte>"),
    CHARARRAY("Array<Char>"),
    BIGDECIMALARRAY("Array<BigDecimal>"),
    BIGINTEGERARRAY("Array<BigInteger>"),
    ADDRESS("Address"),
    ;

    override fun toString() = type
}

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
data class Address(

    @JsonProperty("PostalCode")
    var postalCode: String? = null,

    @JsonProperty("Street")
    var street: String? = null,

    @JsonProperty("City")
    var city: String? = null,

    @JsonProperty("State")
    var state: String? = null,

    @JsonProperty("Country")
    var country: String? = null,

    @JsonProperty("Latitude")
    var latitude: String? = null,

    @JsonProperty("Longitude")
    var longitude: String? = null,
)

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
// TODO Untested
data class Location(

    @JsonProperty("Latitude")
    var latitude: String? = null,

    @JsonProperty("Longitude")
    var longitude: String? = null,
)
