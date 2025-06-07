package me.campos.corp.jnforce.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

// Might need to doublecheck this and move to separate file
// Salesforce defaults to 18 digits
enum class MappedType(val type: String) {
    ADDRESS("Address"),
    STRING("String"),
    BOOLEAN("Boolean"),
    INT("Int"),
    LONG("Long"),
    DOUBLE("Double"),
    DATE("Date"),
    DATETIME("Datetime"), // --> Test
    BASE64("Base64"),
    ID("ID"),
    REFERENCE("Reference"),
    CURRENCY("Currency"),
    TEXTAREA("Textarea"),
    PERCENT("Percentage"),
    PHONE("Phone"),
    URL("Url"),
    EMAIL("Email"),
    COMBOBOX("Combobox"),
    PICKLIST("Picklists"),
    MULTIPICKLIST("Multipicklist"),
    ANYTYPE("Anytype"),
    LOCATION("Location"), // Also Geolocation could be in Grade, Minute Second or Decimal so check this
    NUMBER("Number"), // BigDecimal or BigInteger??
    ;

    override fun toString() = type
}

// enum class MappedType(val type: String) {
//     ADDRESS("Address"),
//     BOOLEAN("Boolean"),
//     CHECKBOX("Checkbox__c"),
//     CURRENCY("Currency__c"),
//     STRING("String"),
//     INT("Int"),
//     LOCALDATE("LocalDate"),
//     DOUBLE("Double"),
//     FLOAT("Float"),
//     LONG("Long"),
//     SHORT("Short"),
//     BYTE("Byte"),
//     CHAR("Char"),
//     BIGDECIMAL("BigDecimal"),
//     BIGINTEGER("BigInteger"),
//     OBJECT("Object"),
//     STRINGARRAY("Array<String>"),
//     INTARRAY("Array<Int>"),
//     LOCALDATEARRAY("Array<LocalDate>"),
//     BOOLEANARRAY("Array<Boolean>"),
//     DOUBLEARRAY("Array<Double>"),
//     FLOATARRAY("Array<Float>"),
//     LONGARRAY("Array<Long>"),
//     SHORTARRAY("Array<Short>"),
//     BYTEARRAY("Array<Byte>"),
//     CHARARRAY("Array<Char>"),
//     BIGDECIMALARRAY("Array<BigDecimal>"),
//     BIGINTEGERARRAY("Array<BigInteger>"),
//     ;
//     override fun toString() = type
// }

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
)

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
data class Location(

    @JsonProperty("latitude")
    var latitude: Double? = null,

    @JsonProperty("longitude")
    var longitude: Double? = null,
)
