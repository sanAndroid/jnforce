package com.github.sanandroid.jlsforce.dataclassdsl

// It maybe it is better to leave the value nullable instead of using the default value
interface BaseBuilder {
    fun build(): String
}

open class Builder<T> : BaseBuilder {
    var value: T? = null
    fun set(lambda: Builder<T>.() -> T): Builder<T> {
        value = lambda()
        return this
    }

    override fun build(): String = value?.toString() ?: ""
}

abstract class ElementsBuilder : ArrayList<String?>(), BaseBuilder {
    fun addElement(builder: Builder<String>.() -> String) =
        add(Builder<String>().set(builder).build())

    override fun build(): String = if (isEmpty()) "" else joinToString(separator = "\n")
}

class ImportsBuilder : ElementsBuilder() {
    fun import(importBuilder: Builder<String>.() -> String) {
        add(ImportBuilder().set(importBuilder).build())
    }

    class ImportBuilder : Builder<String>() {
        override fun build(): String = "import $value"
    }
}

class AnnotationsBuilder : ElementsBuilder() {
    fun annotation(elementBuilder: Builder<String>.() -> String) = super.addElement(elementBuilder)
}

class ModifiersBuilder : ElementsBuilder() {
    fun modifier(elementBuilder: Builder<String>.() -> String) = super.addElement(elementBuilder)
    override fun build(): String = if (isEmpty()) "" else joinToString(" ")
}

class ParametersBuilder : ElementsBuilder() {
    fun parameter(parameterBuilder: ParameterBuilder.() -> Unit) {
        add(ParameterBuilder().apply(parameterBuilder).build())
    }
}
