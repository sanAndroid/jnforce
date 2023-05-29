package com.github.sanandroid.jlsforce.dataclassdsl

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import me.campos.corp.jlsforce.model.Address
import me.campos.corp.jlsforce.model.Location

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
data class Lead(
    @JsonProperty("Id")
    val id: String? = null,
    @JsonProperty("IsDeleted")
    val isDeleted: Boolean,
    @JsonProperty("MasterRecordId")
    val masterRecordId: String? = null,
    @JsonProperty("LastName")
    var lastName: String,
    @JsonProperty("FirstName")
    var firstName: String? = null,
    @JsonProperty("Salutation")
    var salutation: String? = null,
    @JsonProperty("Name")
    val name: String,
    @JsonProperty("Title")
    var title: String? = null,
    @JsonProperty("Company")
    var company: String,
    @JsonProperty("Street")
    var street: String? = null,
    @JsonProperty("City")
    var city: String? = null,
    @JsonProperty("State")
    var state: String? = null,
    @JsonProperty("PostalCode")
    var postalCode: String? = null,
    @JsonProperty("Country")
    var country: String? = null,
    @JsonProperty("Latitude")
    var latitude: Double? = null,
    @JsonProperty("Longitude")
    var longitude: Double? = null,
    @JsonProperty("GeocodeAccuracy")
    var geocodeAccuracy: String? = null,
    @JsonProperty("Address")
    val address: Address? = null,
    @JsonProperty("Phone")
    var phone: String? = null,
    @JsonProperty("MobilePhone")
    var mobilePhone: String? = null,
    @JsonProperty("Fax")
    var fax: String? = null,
    @JsonProperty("Email")
    var email: String? = null,
    @JsonProperty("Website")
    var website: String? = null,
    @JsonProperty("PhotoUrl")
    val photoUrl: String? = null,
    @JsonProperty("Description")
    var description: String? = null,
    @JsonProperty("LeadSource")
    var leadSource: String? = null,
    @JsonProperty("Status")
    var status: String,
    @JsonProperty("Industry")
    var industry: String? = null,
    @JsonProperty("Rating")
    var rating: String? = null,
    @JsonProperty("AnnualRevenue")
    var annualRevenue: Double? = null,
    @JsonProperty("NumberOfEmployees")
    var numberOfEmployees: Int? = null,
    @JsonProperty("OwnerId")
    var ownerId: String,
    @JsonProperty("IsConverted")
    val isConverted: Boolean,
    @JsonProperty("ConvertedDate")
    val convertedDate: String? = null,
    @JsonProperty("ConvertedAccountId")
    val convertedAccountId: String? = null,
    @JsonProperty("ConvertedContactId")
    val convertedContactId: String? = null,
    @JsonProperty("ConvertedOpportunityId")
    val convertedOpportunityId: String? = null,
    @JsonProperty("IsUnreadByOwner")
    var isUnreadByOwner: Boolean,
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
    @JsonProperty("LastActivityDate")
    val lastActivityDate: String? = null,
    @JsonProperty("LastViewedDate")
    val lastViewedDate: String? = null,
    @JsonProperty("LastReferencedDate")
    val lastReferencedDate: String? = null,
    @JsonProperty("Jigsaw")
    var jigsaw: String? = null,
    @JsonProperty("JigsawContactId")
    val jigsawContactId: String? = null,
    @JsonProperty("CleanStatus")
    var cleanStatus: String? = null,
    @JsonProperty("CompanyDunsNumber")
    var companyDunsNumber: String? = null,
    @JsonProperty("DandbCompanyId")
    var dandbCompanyId: String? = null,
    @JsonProperty("EmailBouncedReason")
    var emailBouncedReason: String? = null,
    @JsonProperty("EmailBouncedDate")
    var emailBouncedDate: String? = null,
    @JsonProperty("IndividualId")
    var individualId: String? = null,
    @JsonProperty("SICCode__c")
    var sICCode: String? = null,
    @JsonProperty("ProductInterest__c")
    var productInterest: String? = null,
    @JsonProperty("Primary__c")
    var primary: String? = null,
    @JsonProperty("CurrentGenerators__c")
    var currentGenerators: String? = null,
    @JsonProperty("NumberofLocations__c")
    var numberofLocations: Double? = null,
    @JsonProperty("GeoLocation__Latitude__s")
    var geoLocationLatitude: Double,
    @JsonProperty("GeoLocation__Longitude__s")
    var geoLocationLongitude: Double,
    @JsonProperty("GeoLocation__c")
    val geoLocation: Location,
    @JsonProperty("Checkbox__c")
    var checkbox: Boolean,
    @JsonProperty("Currency__c")
    var currency: Double? = null,
    @JsonProperty("Date__c")
    var date: String? = null,
)
