package me.campos.corp.jaleson.poet

import com.github.sanandroid.jaleson.dataclassdsl.AnnotationsBuilder
import com.github.sanandroid.jaleson.dataclassdsl.Builder
import com.github.sanandroid.jaleson.dataclassdsl.ImportsBuilder
import com.github.sanandroid.jaleson.dataclassdsl.ModifiersBuilder
import com.github.sanandroid.jaleson.dataclassdsl.ParametersBuilder

fun dataClass(lambda: DataClassBuilder.() -> Unit) =
    DataClassBuilder().apply(lambda)

class DataClassBuilder {

    val packageName = Builder<String>()
    fun packageName(name: Builder<String>.() -> String) {
        packageName.set(name)
    }

    val importsBuilder = ImportsBuilder()
    fun imports(lambda: ImportsBuilder.() -> Unit) = importsBuilder.apply(lambda)

    val annotationsBuilder = AnnotationsBuilder()
    fun annotations(lambda: AnnotationsBuilder.() -> Unit) = annotationsBuilder.apply(lambda)

    val modifiersBuilder = ModifiersBuilder()
    fun modifiers(lambda: ModifiersBuilder.() -> Unit) = modifiersBuilder.apply(lambda)

    val className = Builder<String>()
    fun name(name: Builder<String>.() -> String) {
        className.set(name)
    }

    val parametersBuilder: ParametersBuilder = ParametersBuilder()
    fun parameters(lambda: ParametersBuilder.() -> Unit) = parametersBuilder.apply(lambda)

    fun build(): String =
        StringBuilder()
            .appendLine("package " + packageName.build())
            .appendLine()
            .appendLine(importsBuilder.build())
            .appendLine()
            .appendLine(annotationsBuilder.build())
            .appendLine("${modifiersBuilder.build()} data class ${className.build()}(")
            .appendLine(parametersBuilder.build())
            .appendLine(")")
            .toString()
}
