package com.github.sanandroid.jlsforce.helpers

import java.io.File
// TODO change / to java.io.File.separator?
fun writeFileDirectlyAsText(path: String = "", fileName: String, fileContent: String) =
    File("$path$fileName").writeText(fileContent, Charsets.UTF_8)

fun readFileDirectlyAsText(path: String = "", fileName: String) = File("$path$fileName").readText(Charsets.UTF_8)
