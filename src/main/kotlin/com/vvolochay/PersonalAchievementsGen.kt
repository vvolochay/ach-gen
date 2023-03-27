package com.vvolochay

import java.io.File

data class ContestantData(val id: String, val name: String, val region: String, val additionalInfo: String)

class PersonalAchievementsGen : Generator() {
    override fun run(filename: File, logo: File, template: File, outputDir: String, result: Boolean) {
        if (filename.isDirectory) {
            throw IllegalArgumentException("${AchType.Person} supports only one file parsing")
        }

        filename.forEachLine {
            val info = parseLines(it)
            val replaced = template.readText(Charsets.UTF_8)
                .replace("{Name}", info.name)
                .replace("{Region}", info.region)
                .replace("{additionalInfo}", info.additionalInfo)
                .replace("{Logo}", base64Logo(if (logo.isDirectory) File(logo.path + "/" + info.id + ".png") else logo))

            File("$outputDir/${info.id}.svg").writeText(replaced, Charsets.UTF_8)
        }
    }

    private fun parseLines(str: String): ContestantData {
        val data = str.split("|").map { s -> s.trim() }
        return ContestantData(data[0], data[1], data[2], data[3])
    }
}