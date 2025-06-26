package com.github.sanandroid.jnforce.helpers

import java.io.File

fun writeFileDirectlyAsText(path: String = "", fileName: String, fileContent: String) {
    val basePath = path.removePrefix("file://")
    val file = File(basePath, fileName)
    file.parentFile?.mkdirs()
    file.writeText(fileContent, Charsets.UTF_8)
}

fun readFileDirectlyAsText(path: String = "", fileName: String) =
    File(path.removePrefix("file://"), fileName).readText(Charsets.UTF_8)
