package com.vvolochay

import com.google.gson.Gson
import com.google.gson.stream.JsonReader
import com.vvolochay.GenUtils.Companion.base64Logo
import com.vvolochay.GenUtils.Companion.parseColorLibrary
import com.vvolochay.GenUtils.Companion.replaceEscapingSymbols
import java.io.File
import java.io.FileReader

val DEFAULT_COLOR = Color("default", "#FFFFFF", "#2B2B2B")

data class Data(
    val id: Int,
    val university: University,
    val team: Team,
    val coach: Person,
    val contestants: List<Person>
)

data class TeamColor(
    val id: Int,
    val colorName: String,
)

data class Team(val name: String, val regionals: List<String>)

data class University(
    val fullName: String, val shortName: String, val region: String, val hashTag: String?, val url: String,
    val appYears: List<Int>? = null, val winYears: List<Int>? = null, val goldYears: List<Int>? = null,
    val silverYears: List<Int>? = null, val bronzeYears: List<Int>? = null, val regYears: List<Int>? = null
)

data class Person(
    val name: String, val altNames: List<String>, val tcHandle: String? = null, val tcRating: Int? = null,
    val cfHandle: String? = null, val cfRating: Int? = null, val twitterHandle: String? = null,
    val achievements: List<Achievement>? = null
)

data class Achievement(val achievement: String, val priority: Int)

class WFAchievementsGen : Generator() {

    private lateinit var output: String
    private lateinit var logo: File
    private var colors = parseColorLibrary(File("src/main/resources/colors.json"))

    override fun run(filename: File, logo: File, template: File, outputDir: String, result: Boolean) {
        val teamData = parseTeamsData(filename)
        val teamColors = parseTeamsColors(File("data/wf_dhaka/shirts.json"))

        this.output = outputDir
        this.logo = logo

        for (data in teamData) {
            val teamColor = teamColors.getOrElse(data.id - 1) { DEFAULT_COLOR }

            generateMainSVG(data, outputDir, teamColor)
//            generatePersonSVG(data, data.coach, "coach")
//            for (i in 0 until data.contestants.size) {
//                generatePersonSVG(data, data.contestants[i], "contestant_$i")
//            }
        }
    }

    fun generateMainSVG(data: Data, path: String, color: Color) {
        var replaced = if (data.university.fullName.length >= 35) {
            val splitName = splitNameTwoPart(data.university.fullName)

            File("src/main/resources/svg/University_main 2 line.svg").readText(Charsets.UTF_8)
                .replace("{UniversityName1}", splitName.first)
                .replace("{UniversityName2}", splitName.second)

        } else {
            File("src/main/resources/svg/University_main 1 line.svg").readText(Charsets.UTF_8)
                .replace("{UniversityName}", replaceEscapingSymbols(data.university.fullName))
        }

        replaced = replaced
            .replace("{Logo}", base64Logo(if (logo.isDirectory) File(logo.path + "/" + data.id + ".jpg") else logo))
            .replace("{ShortTeamName}", replaceEscapingSymbols(data.team.name))
            .replace("{Region}", replaceEscapingSymbols(data.university.region))
            .replace("{RegionalPlace}", replaceEscapingSymbols(data.team.regionals.last()))
            .replace("{HashTag}", data.university.hashTag ?: "")

        // set font size
        val font = data.team.name.length * 12 + 190
        replaced = replaced.replace("{fontSize}", font.toString())

        //set colors
        replaced = replaced.replace("{mainColor}", color.hex).replace("{fontColor}", color.fontColor)

        File(path, "${data.id}_main.svg").writeText(replaced, Charsets.UTF_8)
    }

    private fun parseTeamsData(dataFiles: File): List<Data> {
        val teamsData = mutableListOf<Data>()
        dataFiles.listFiles()?.forEach {
            val reader = JsonReader(FileReader(it))
            val data: Data = Gson().fromJson(reader, Data::class.java)
            teamsData.add(data)
        }
        return teamsData
    }

    private fun parseTeamsColors(file: File): List<Color> {
        val reader = JsonReader(FileReader(file))
        val teams: Array<TeamColor> = Gson().fromJson(reader, Array<TeamColor>::class.java)

        return teams.map { colors[it.colorName]!! }
    }

    private fun splitNameTwoPart(fullName: String): Pair<String, String> {
        val s = fullName.substring(0, fullName.length * 2 / 3 ).substringBeforeLast(" ")
        return Pair(s.trim(), fullName.substring(s.length, fullName.length).trim())
    }
}

