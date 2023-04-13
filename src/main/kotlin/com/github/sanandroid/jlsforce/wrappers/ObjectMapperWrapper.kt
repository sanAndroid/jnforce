package com.github.sanandroid.jlsforce.wrappers

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper

interface ObjectMapperWrapper {
    fun readTree(json: String): JsonNode
}

/*
* I would prefer to instantiate that using a companion object
 */
class ObjectMapperWrapperImpl : ObjectMapperWrapper {
    private val objectMapper: ObjectMapper
        get() = ObjectMapper()
    override fun readTree(json: String) = objectMapper.readTree(json)
}
