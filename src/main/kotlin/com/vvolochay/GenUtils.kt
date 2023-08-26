package com.vvolochay

import com.google.gson.Gson
import com.google.gson.stream.JsonReader
import org.apache.commons.io.FileUtils
import java.io.File
import java.io.FileReader
import java.util.*

data class Color(
    val name: String,
    val hex: String,
    val fontColor: String
)

class GenUtils {
    companion object {

        fun parseColorLibrary(colors: File): Map<String, Color> {
            val reader = JsonReader(FileReader(colors))
            val list: Array<Color> = Gson().fromJson(reader, Array<Color>::class.java)

            return list.associateBy { it.name }
        }

        fun base64Logo(logo: File): String {
            val fileContent: ByteArray = FileUtils.readFileToByteArray(logo)
            return Base64.getEncoder().encodeToString(fileContent)
        }

        /**
         * fix non-supported symbols ascii -> html
         */
        fun replaceEscapingSymbols(str: String): String {
            return str.replace("&", "&amp;").replace("<", "&#60;")
        }
    }
}