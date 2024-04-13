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

        fun placeRating(rating: String, x: Int, r: String): String {
            val ratingCircle = "<g id=\"Frame 1\">\n" +
                    "<g id=\"Rating circle\">\n" +
                    "        <rect x=\"{X_coord}\" y=\"92.9756\" width=\"31.0488\" height=\"29.0488\" rx=\"14.5244\"\n" +
                    "     fill=\"{fontColor}\" fill-opacity=\"0.8\"/>\n" +
                    "        <svg x=\"{X_coord}\" y=\"92.9756\" width=\"31.0488\" height=\"30.0488\">\n" +
                    "        <text id=\"rate\" fill=\"{mainColor}\" xml:space=\"preserve\" style=\"white-space: pre\"\n" +
                    "     font-family=\"Helvetica\" x=\"50%\" y=\"50%\"\n" +
                    "alignment-baseline=\"middle\" text-anchor=\"middle\" font-size=\"11.2341\" font-weight=\"800\"\n" +
                    "   letter-spacing=\"0em\">{R}</text>\n" +
                    "       </svg>\n" +
                    "       </g>\n" +
                    "        <text id=\"Rating\" fill=\"{fontColor}\" fill-opacity=\"0.8\" xml:space=\"preserve\"\n" +
                    "    style=\"white-space: pre\" font-family=\"Helvetica\" font-size=\"16.8512\"\n" +
                    "     font-weight=\"800\" letter-spacing=\"0em\"><tspan x=\"{X_coord_2}\" y=\"113.398\">{Rating}</tspan>\n" +
                    "       </text>\n" +
                    " </g>"

            return ratingCircle.replace("{Rating}", rating)
                .replace("{X_coord_2}", (x + 39).toString())
                .replace("{X_coord}", x.toString())
                .replace("{R}", r)
        }
    }
}