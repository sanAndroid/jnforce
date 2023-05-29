package com.github.sanandroid.jlsforce.settings

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.sanandroid.jlsforce.helpers.readFileDirectlyAsText
import org.junit.Assert.*
import org.junit.Test

class JlsForceComponentTest {

    private val sObject: JsonNode
        get() = readFileDirectlyAsText("src/test/testData/settings/", "SObject.json").let {
            ObjectMapper().readTree(it)
        }

    @Test
    fun testCreatable() {
        assertNotNull(sObject.get(CREATEABLE))
    }

    @Test
    fun testCustom() {
        assertNotNull(sObject.get(CUSTOM))
    }

    @Test
    fun testDeletable() {
        assertNotNull(sObject.get(DELETABLE))
    }

    @Test
    fun testLayoutable() {
        assertNotNull(sObject.get(LAYOUTABLE))
    }

    @Test
    fun testMergeable() {
        assertNotNull(sObject.get(MERGEABLE))
    }

    @Test
    fun testReplicateable() {
        assertNotNull(sObject.get(REPLICATEABLE))
    }

    @Test
    fun testRetrieveable() {
        assertNotNull(sObject.get(RETRIEVEABLE))
    }

    @Test
    fun testSearchable() {
        assertNotNull(sObject.get(SEARCHABLE))
    }

    @Test
    fun testUpdateable() {
        assertNotNull(sObject.get(UPDATEABLE))
    }
}