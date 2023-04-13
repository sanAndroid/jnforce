package com.github.sanandroid.jlsforce.dataclassdsl

import com.github.sanandroid.jlsforce.helpers.readFileDirectlyAsText
import org.junit.Assert.assertEquals
import org.junit.Test

internal class SalizerTest {

    @Test
    fun dataClassFromJsonForJackson() {
        val leadAsJson = readFileDirectlyAsText("src/test/testData/salizer/", "lead.json")
        val leadAsDataclass = readFileDirectlyAsText("src/test/testData/salizer/", "Lead.kt").replace("\\p{Zs}+".toRegex(), "")
        val uut = Salizer().dataClassFromJsonForJackson(leadAsJson, "com.github.sanandroid.jlsforce.model").replace("\\p{Zs}+".toRegex(), "")
        assertEquals(uut, leadAsDataclass)
    }
}
