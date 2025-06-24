package com.github.sanandroid.jnforce.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.github.sanandroid.jnforce.model.Address
import com.github.sanandroid.jnforce.model.Location

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
data class AllField(
    @JsonProperty("Id")
    val id: String? = null,
    @JsonProperty("IsDeleted")
    val isDeleted: Boolean,
    @JsonProperty("Name")
    var name: String? = null,
    @JsonProperty("CreatedDate")
    val createdDate: String,
    @JsonProperty("CreatedById")
    val createdById: String,
    @JsonProperty("LastModifiedDate")
    val lastModifiedDate: String,
    @JsonProperty("LastModifiedById")
    val lastModifiedById: String,
    @JsonProperty("SystemModstamp")
    val systemModstamp: String,
    @JsonProperty("LastViewedDate")
    val lastViewedDate: String? = null,
    @JsonProperty("LastReferencedDate")
    val lastReferencedDate: String? = null,
    @JsonProperty("Auto_Number__c")
    val autoNumber: String,
    @JsonProperty("Lookup_Field__c")
    var lookupField: String? = null,
    @JsonProperty("Master_Detail_Relationship__c")
    val masterDetailRelationship: String,
    @JsonProperty("Checkbox__c")
    var checkbox: Boolean,
    @JsonProperty("Currency__c")
    var currency: String? = null,
    @JsonProperty("Date__c")
    var date: String? = null,
    @JsonProperty("Date_Time__c")
    var dateTime: String? = null,
    @JsonProperty("Email__c")
    var email: String? = null,
    @JsonProperty("GeoLocation__Latitude__s")
    var geoLocationLatitudes: Double? = null,
    @JsonProperty("GeoLocation__Longitude__s")
    var geoLocationLongitudes: Double? = null,
    @JsonProperty("GeoLocation__c")
    val geoLocation: String? = null,
    @JsonProperty("Number__c")
    var number: Double? = null,
    @JsonProperty("Percent__c")
    var percent: String? = null,
    @JsonProperty("Phone__c")
    var phone: String? = null,
    @JsonProperty("Picklist__c")
    var picklist: String? = null,
    @JsonProperty("Picklist_Multi_Select__c")
    var picklistMultiSelect: String? = null,
    @JsonProperty("Text__c")
    var text: String? = null,
    @JsonProperty("Text_Area_Long__c")
    var textAreaLong: String? = null,
    @JsonProperty("Text_Area_Rich__c")
    var textAreaRich: String? = null,
    @JsonProperty("Text_Encrypted__c")
    var textEncrypted: String? = null,
    @JsonProperty("Time__c")
    var time: String? = null,
    @JsonProperty("URL__c")
    var uRL: String? = null,
    @JsonProperty("Address__Street__s")
    var addressStreets: String? = null,
    @JsonProperty("Address__City__s")
    var addressCitys: String? = null,
    @JsonProperty("Address__PostalCode__s")
    var addressPostalCodes: String? = null,
    @JsonProperty("Address__StateCode__s")
    var addressStateCodes: String? = null,
    @JsonProperty("Address__CountryCode__s")
    var addressCountryCodes: String? = null,
    @JsonProperty("Address__Latitude__s")
    var addressLatitudes: Double? = null,
    @JsonProperty("Address__Longitude__s")
    var addressLongitudes: Double? = null,
    @JsonProperty("Address__GeocodeAccuracy__s")
    var addressGeocodeAccuracys: String? = null,
    @JsonProperty("Address__c")
    val address: Address? = null,
    @JsonProperty("MinuteLocation__Latitude__s")
    var minuteLocationLatitudes: Double? = null,
    @JsonProperty("MinuteLocation__Longitude__s")
    var minuteLocationLongitudes: Double? = null,
    @JsonProperty("MinuteLocation__c")
    val minuteLocation: String? = null,
)
