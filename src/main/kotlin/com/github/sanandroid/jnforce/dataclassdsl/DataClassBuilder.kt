package me.campos.corp.jnforce.poet

import com.github.sanandroid.jnforce.dataclassdsl.AnnotationsBuilder
import com.github.sanandroid.jnforce.dataclassdsl.Builder
import com.github.sanandroid.jnforce.dataclassdsl.ImportsBuilder
import com.github.sanandroid.jnforce.dataclassdsl.ModifiersBuilder
import com.github.sanandroid.jnforce.dataclassdsl.ParametersBuilder

fun dataClass(lambda: DataClassBuilder.() -> Unit) =
    DataClassBuilder().apply(lambda)

class DataClassBuilder {

    private val packageName = Builder<String>()
    fun packageName(name: Builder<String>.() -> String) {
        packageName.set(name)
    }

    private val importsBuilder = ImportsBuilder()
    fun imports(lambda: ImportsBuilder.() -> Unit) = importsBuilder.apply(lambda)

    private val annotationsBuilder = AnnotationsBuilder()
    fun annotations(lambda: AnnotationsBuilder.() -> Unit) = annotationsBuilder.apply(lambda)

    private val modifiersBuilder = ModifiersBuilder()
    @Suppress("UNUSED")
    fun modifiers(lambda: ModifiersBuilder.() -> Unit) = modifiersBuilder.apply(lambda)

    private val className = Builder<String>()
    fun name(name: Builder<String>.() -> String) {
        className.set(name)
    }

    private val parametersBuilder: ParametersBuilder = ParametersBuilder()
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
