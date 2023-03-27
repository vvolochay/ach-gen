package com.vvolochay

import org.apache.commons.io.FileUtils
import java.io.File
import java.util.*

fun base64Logo(logo: File): String {
    val fileContent: ByteArray = FileUtils.readFileToByteArray(logo)
    return Base64.getEncoder().encodeToString(fileContent)
}
