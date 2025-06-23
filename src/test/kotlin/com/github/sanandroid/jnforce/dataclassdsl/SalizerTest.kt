package com.github.sanandroid.jnforce.dataclassdsl

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.sanandroid.jnforce.helpers.readFileDirectlyAsText
import com.github.sanandroid.jnforce.model.Address
import com.github.sanandroid.jnforce.model.Location
import org.junit.Assert.assertEquals
import org.junit.Test

internal class SalizerTest {

    @Test
    fun leadDataClassFromJsonForJackson() {
        val leadAsJson = readFileDirectlyAsText("src/test/testData/salizer/", "lead_meta.json")
        val leadAsDataclass =
            readFileDirectlyAsText("src/test/kotlin/com/github/sanandroid/jnforce/dataclassdsl/", "Lead.kt")
                .replace("\\p{Zs}+".toRegex(), "")
        val uut = Salizer().dataClassFromJsonForJackson(leadAsJson, "com.github.sanandroid.jnforce.dataclassdsl")
            .replace("\\p{Zs}+".toRegex(), "")
        assertEquals(uut, leadAsDataclass)
    }

    @Test
    fun allFieldsDataClassFromJsonForJackson() {
        val allFieldsAsJson = readFileDirectlyAsText("src/test/testData/salizer/", "all_fields_meta.json")
        val uut = Salizer().dataClassFromJsonForJackson(allFieldsAsJson, "com.github.sanandroid.jnforce.dataclassdsl")
            .replace("\\p{Zs}+".toRegex(), "")
        val leadAsDataclass =
            readFileDirectlyAsText("src/test/kotlin/com/github/sanandroid/jnforce/dataclassdsl/", "AllFields.kt").replace("\\p{Zs}+".toRegex(), "")
        assertEquals(uut, leadAsDataclass)
    }

    @Test
    fun serializeAllFieldsUsingCreatedDataClass() {
        val allFieldsData = readFileDirectlyAsText("src/test/testData/salizer/", "all_fields_data.json")
        val uut = ObjectMapper().readValue(allFieldsData, AllField::class.java)!!
        assertEquals(uut, allFieldReference)
    }

    private val allFieldReference = AllField(
        id = "a00Dm000002ph9CIAQ",
        isDeleted = false,
        name = "Test Field",
        createdDate = "2023-05-23T17:20:36.000+0000",
        createdById = "005Dm000000HHtTIAW",
        lastModifiedDate = "2023-05-23T17:25:37.000+0000",
        lastModifiedById = "005Dm000000HHtTIAW",
        systemModstamp = "2023-05-23T17:25:37.000+0000",
        lastViewedDate = "2023-05-23T17:27:43.000+0000",
        lastReferencedDate = "2023-05-23T17:27:43.000+0000",
        autoNumber = "0",
        lookupField = "001Dm00000E5SP8IAN",
        masterDetailRelationship = "001Dm00000E5SP8IAN",
        checkbox = false,
        currency = 100.0,
        date = "2023-05-05",
        dateTime = "2023-05-19T18:00:00.000+0000",
        email = "testuser@salesforce.com",
        geoLocationLatitude = 12.1212211,
        geoLocationLongitude = 13.12312321,
        geoLocation = Location(
            latitude = 12.1212211,
            longitude = 13.12312321
        ),
        number = 23123.2123123,
        percent = 23.23,
        phone = "+49123212212323",
        picklist = "Another Value",
        picklistMultiSelect = "Multi Second,;Multi Fourth",
        text = "Text Field",
        textAreaLong = "Long Text Field",
        textAreaRich = "<p><strong>Rich <em>Text </em></strong>is here! Also in <span style=\"color: rgb(255, 85, 0);\">red</span></p>",
        textEncrypted = "************",
        time = "00:30:00.000Z",
        addressCity = "CDMX",
        addressCountryCode = "MX",
        addressGeocodeAccuracy = null,
        addressLatitude = null,
        addressLongitude = null,
        addressPostalCode = "06760",
        addressStateCode = "DF",
        addressStreet = "Tonala 348",
        uRL = "https://google.de",
        address = Address(
            city = "CDMX",
            country = "México",
            countryCode = "MX",
            geocodeAccuracy = null,
            latitude = null,
            longitude = null,
            postalCode = "06760",
            state = "Distrito Federal Mexicano",
            stateCode = "DF",
            street = "Tonala 348"
        ),
        minuteLocationLatitude = 31.121,
        minuteLocationLongitude = 22.123123,
        minuteLocation = Location(
            latitude = 31.121,
            longitude = 22.123123
        )
    )
}
