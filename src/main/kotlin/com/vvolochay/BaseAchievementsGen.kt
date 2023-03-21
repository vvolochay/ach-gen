package com.vvolochay

import org.apache.commons.io.FileUtils
import java.io.File
import java.util.*

data class TeamData(
    val id: String, val hashTag: String, val university: String, val teamName: String, val contestants: String
)

class BaseAchievementsGen {

    fun run(filename: File) {
        filename.forEachLine {
            val teamInfo = parseData(it)

            val replaced = File("src/main/resources/base/simple1.svg").readText(Charsets.UTF_8)
                .replace("{University}", teamInfo.university)
                .replace("{HashTag}", teamInfo.hashTag)
                .replace("{Contestants}", teamInfo.contestants)
                .replace("{TeamName}", teamInfo.teamName)
                .replace("{UniversityLogo}", base64Logo())
            File("$outputDir/${teamInfo.id}.svg").writeText(replaced, Charsets.UTF_8)
        }
    }

    private fun parseData(str: String): TeamData {
        val teamData = str.split("|").map { s -> s.trim() }
        return TeamData(teamData[0], teamData[1], teamData[2], teamData[3], teamData[4])
    }

    fun base64Logo(imagePath: String = "src/main/resources/images/base_logo.png"): String {
        val fileContent: ByteArray = FileUtils.readFileToByteArray(File(imagePath))
        return Base64.getEncoder().encodeToString(fileContent)
    }

}