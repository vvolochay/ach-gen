package com.vvolochay

import com.vvolochay.GenUtils.Companion.base64Logo
import java.io.File

data class ContestantData(
    val id: String,
    val name: String,
    val region: String,
    val additionalInfo: String,
    val diploma: String,
    val points: String
)

class PersonalAchievementsGen : Generator() {
    override fun run(filename: File, logo: File, template: File, outputDir: String, result: Boolean) {
        if (filename.isDirectory) {
            throw IllegalArgumentException("${AchType.Person} supports only one file parsing")
        }

        filename.forEachLine {
            val info = parseLines(it)
            var replaced = template.readText(Charsets.UTF_8)
                .replace("{Name}", info.name)
                .replace("{Logo}", base64Logo(logo, info.id.toInt(), File("")))

            replaced = if (result) {
                replaceVsoshData(replaced, info.points)
                    .replace("{Region}", "${info.additionalInfo}, ${info.region}")
                    .replace("{additionalInfo}", info.diploma)
            } else {
                replaced.replace("{Region}", info.region)
                    .replace("{additionalInfo}", info.additionalInfo)
            }

            File("$outputDir/${info.id}.svg").writeText(replaced, Charsets.UTF_8)
        }
    }

    private fun replaceVsoshData(s: String, p: String): String {
        val points = p.split("\t").map { i -> i.toInt() }
        if (points.size != 9) throw IllegalArgumentException("Not enough points in input, expected 9, but actual ${points.size}")
        return s
            .replace("{d1}", points.subList(0, 4).sum().toString())
            .replace("{d2}", points.subList(4, 8).sum().toString())
            .replace("{sum}", points.last().toString())
    }

    private fun parseLines(str: String): ContestantData {
        val data = str.split("|").map { s -> s.trim() }
        return if (data.size == 4) ContestantData(data[0], data[1], data[2], data[3], "", "")
        else ContestantData(data[0], data[1], data[2], data[3], data[4], data[5])
    }
}