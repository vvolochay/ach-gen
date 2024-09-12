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

        fun base64Logo(logo: File, id: Int, defaultLogo: File): String {
            var logoPath = defaultLogo
            if (logo.isDirectory) {
                val path = File(logo.path + "/" + id + ".png")
                if (path.exists()) {
                    logoPath = path
                } else {
                    println("LOGO MISSED $id")
                }
            }

            val fileContent: ByteArray = FileUtils.readFileToByteArray(logoPath)
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
                    "        <rect x=\"{X_coord}\" y=\"89\" width=\"31.0488\" height=\"29.0488\" rx=\"14.5244\"\n" +
                    "     fill=\"{fontColor}\" fill-opacity=\"0.8\"/>\n" +
                    "        <svg x=\"{X_coord}\" y=\"89\" width=\"31.0488\" height=\"30.0488\">\n" +
                    "        <text id=\"rate\" fill=\"{mainColor}\" xml:space=\"preserve\" style=\"white-space: pre\"\n" +
                    "     font-family=\"Helvetica\" x=\"50%\" y=\"50%\"\n" +
                    "alignment-baseline=\"middle\" text-anchor=\"middle\" font-size=\"11.2341\" font-weight=\"800\"\n" +
                    "   letter-spacing=\"0em\">{R}</text>\n" +
                    "       </svg>\n" +
                    "       </g>\n" +
                    "        <text id=\"Rating\" fill=\"{fontColor}\" fill-opacity=\"0.8\" xml:space=\"preserve\"\n" +
                    "    style=\"white-space: pre\" font-family=\"Helvetica\" font-size=\"16.8512\"\n" +
                    "     font-weight=\"800\" letter-spacing=\"0em\"><tspan x=\"{X_coord_2}\" y=\"110.398\">{Rating}</tspan>\n" +
                    "       </text>\n" +
                    " </g>"

            return ratingCircle.replace("{Rating}", rating)
                .replace("{X_coord_2}", (x + 39).toString())
                .replace("{X_coord}", x.toString())
                .replace("{R}", r)
        }

        fun addMedals(replaced: String, university: University): String {
            val gold = university.goldYears?.size ?: 0
            val silver = university.silverYears?.size ?: 0
            val bronze = university.bronzeYears?.size ?: 0

            if (gold == 0 && silver == 0 && bronze == 0) return replaced

            return replaced
                .replace("{Medals}", MEDALS)
                .replace("x G", "x $gold")
                .replace("x S", "x $silver")
                .replace("x B", "x $bronze")

        }
        fun addCups(replaced: String, university: University, startsX: Double): String {
            var cup = CUP
            var allCups = ""
            var startsFrom = startsX
            var textFrom = startsFrom + 18
            var distX = 70

            if (university.winYears != null) {
                if (university.winYears.size >= 5) {  // ITMO hack
                    distX = 50
                }

                for (year in university.winYears) {
                    val newCup = cup
                        .replace("{year}", yearMapping(year))
                        .replace("{xCoord}", startsFrom.toString())
                        .replace("{textXCoord}", textFrom.toString())
                    allCups += newCup
                    startsFrom += distX
                    textFrom = startsFrom + 18
                    cup = CUP
                }
            }
            return replaced.replace("{AllCups}", allCups)
        }

        fun addRC(replaced: String, university: University, lastX: Double): String {
            var startsFrom = lastX
            var rc = RC
            var distX = 100
            var allRC = ""
            if (university.regYears != null) {

                val size = university.regYears.size
                if (size > 3) {
                    allRC += rc
                        .replace("{year}", "x $size")
                        .replace("{xCoord}", startsFrom.toString())
                        .replace("{x1GradientCoord}", (startsFrom + 100).toString())
                        .replace("{svgCoord}", (startsFrom - 20).toString())
                        .replace("{textCoord}", (startsFrom + 10).toString())
                } else {
                    for (year in university.regYears) {
                        val newRC = rc
                            .replace("{year}", yearMapping(year))
                            .replace("{xCoord}", startsFrom.toString())
                            .replace("{x1GradientCoord}", (startsFrom + 80).toString())
                            .replace("{svgCoord}", (startsFrom - 20).toString())
                            .replace("{textCoord}", (startsFrom + 10).toString())
                            .replace("gradientName", "gradientName$year")

                        allRC += newRC
                        startsFrom += distX
                        rc = RC
                    }
                }
            }
            return replaced.replace("{AllRC}", allRC)
        }

        fun yearMapping(year: Int) : String {
            return when(year) {
                2020 -> "Moscow"
                2021 -> "Dhaka"
                2022 -> "Luxor"
                2023 -> "Luxor"
                else -> {year.toString()}
            }
        }
    }
}