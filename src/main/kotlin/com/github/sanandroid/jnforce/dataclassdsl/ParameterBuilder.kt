package com.github.sanandroid.jnforce.dataclassdsl

import com.github.sanandroid.jnforce.model.MappedType

class ParameterBuilder {
    private val annotationsBuilder = AnnotationsBuilder()
    fun annotations(lambda: AnnotationsBuilder.() -> Unit) = annotationsBuilder.apply(lambda)

    private val modifiersBuilder = ModifiersBuilder()
    @Suppress("UNUSED")
    fun modifiers(lambda: ModifiersBuilder.() -> Unit) = modifiersBuilder.apply(lambda)

    private val typeBuilder = Builder<MappedType>()
    fun type(type: Builder<MappedType>.() -> MappedType) {
        typeBuilder.set(type)
    }

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
