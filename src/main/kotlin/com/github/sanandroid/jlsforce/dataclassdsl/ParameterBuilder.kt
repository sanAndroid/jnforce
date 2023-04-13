package com.github.sanandroid.jlsforce.dataclassdsl

import me.campos.corp.jlsforce.model.MappedType

class ParameterBuilder {
    // TODO this should also use the builder pattern - maybe this will just create uneccary overhead?
    fun annotations(lambda: AnnotationsBuilder.() -> Unit) = annotationsBuilder.apply(lambda)
    private val annotationsBuilder = AnnotationsBuilder()
    private val modifiersBuilder = ModifiersBuilder()

    private val typeBuilder = Builder<MappedType>()
    fun type(type: Builder<MappedType>.() -> MappedType) {
        typeBuilder.set(type)
    }

    // TODO Maybe out this in a container
    private val mutableBuilder = Builder<Mutable>()
    fun mutable(mutable: Builder<Mutable>.() -> Mutable) {
        mutableBuilder.set(mutable)
    }

    fun name(name: Builder<String>.() -> String) {
        nameBuilder.set(name)
    }

    private val nameBuilder = Builder<String>()

    private val optionalBuilder = Builder<Boolean>()
    fun optional(optional: Builder<Boolean>.() -> Boolean) {
        optionalBuilder.set(optional)
    }

    private val defaultValueBuilder = object : Builder<String?>() {
        override fun build() = value?.run {
            " = " +
                if (typeBuilder.build() == "String" && this != "null") {
                    "\"$this\""
                } else {
                    this
                }
        } ?: ""
    }

    fun defaultValue(defaultValue: () -> String?) {
        defaultValueBuilder.value = defaultValue()
    }

    fun build(): String {
        // Id should be optional since salesforce will set it
        if (nameBuilder.build() == "id") {
            optional { true }
            defaultValue { "null" }
        }
        return """
                ${annotationsBuilder.build()}${modifiersBuilder.build()} 
                ${mutableBuilder.build()} ${nameBuilder.build()}: ${typeBuilder.build()}${if (optionalBuilder.build() == "true") "?" else ""}${
            defaultValueBuilder.build()
        },""".replaceIndent("    ")
    }
}

enum class Mutable(val va: String) {
    VAR("var"),
    VAL("val"),
    ;

    override fun toString() = va
}
