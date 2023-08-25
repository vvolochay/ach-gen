package com.vvolochay

import org.apache.commons.io.FileUtils
import java.io.File
import java.util.*

fun base64Logo(logo: File): String {
    val fileContent: ByteArray = FileUtils.readFileToByteArray(logo)
    return Base64.getEncoder().encodeToString(fileContent)
}

/**
 * fix non-supported symbols ascii -> html
 */
fun replaceEscapingSymbols(str : String) : String {
    return str.replace("&", "&amp;").replace("<", "&#60;")
}
