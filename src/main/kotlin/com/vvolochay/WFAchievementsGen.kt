package com.vvolochay

import com.google.gson.Gson

import com.google.gson.stream.JsonReader
import com.vvolochay.GenUtils.Companion.addCups
import com.vvolochay.GenUtils.Companion.addMedals
import com.vvolochay.GenUtils.Companion.addRC
import com.vvolochay.GenUtils.Companion.base64Logo
import com.vvolochay.GenUtils.Companion.parseColorLibrary
import com.vvolochay.GenUtils.Companion.placeRating
import com.vvolochay.GenUtils.Companion.replaceEscapingSymbols
import java.io.File
import java.io.FileReader
import kotlin.math.min

val DEFAULT_COLOR = Color("default", "#FFFFFF", "#2B2B2B")
const val DEFAULT_FONT_SIZE = 50

data class Data(
    var id: Int,
    val university: University,
    val team: Team,
    val coach: Person,
    val contestants: List<Person>
)

data class TeamColor(
    val id: Int,
    val colorName: String,
)

data class Team(val name: String, val regionals: List<String>? = null)

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
    private var defaultLogo = File("data/wf-astana/logo.png")
    private var colors = parseColorLibrary(File("src/main/resources/colors.json"))

    override fun run(filename: File, logo: File, template: File, outputDir: String, result: Boolean) {
        val teamData = parseTeamsData(filename)
        val teamColors = parseTeamsColors(File("data/wf-astana/shirts.json"))

        this.output = outputDir
        this.logo = logo

        for (data in teamData) {
            val teamColor = teamColors.getOrElse(data.id - 1) { DEFAULT_COLOR }

            generateMainSVG(data, outputDir, teamColor)
            generatePersonSVG(data.id, data.coach,  outputDir,"coach", teamColor)
            for (i in 0 until data.contestants.size) {
                generatePersonSVG(data.id, data.contestants[i], outputDir, "contestant_$i", teamColor)
            }
            generateFinalsSVG(data, outputDir, teamColor)
        }
    }

    fun generateMainSVG(data: Data, path: String, color: Color?) {
        if (data.team.name.length > 38) {
            println("Long team name ${data.id}")
        }

        var replaced = File("src/main/resources/svg/main_one_line.svg").readText(Charsets.UTF_8)
            .replace("{ShortTeamName}",  replaceEscapingSymbols(data.team.name))
            .replace("{UniversityName}", replaceEscapingSymbols(data.university.fullName))
            .replace("{Logo}", base64Logo(logo, data.id, defaultLogo))
            .replace("{ShortTeamName}", replaceEscapingSymbols(data.team.name))
            .replace("{Region}", replaceEscapingSymbols(data.university.region))
            .replace("{HashTag}", data.university.hashTag ?: "")

        if (!data.team.regionals.isNullOrEmpty()) {
            replaced = replaced.replace("{RegionalPlace}", replaceEscapingSymbols(data.team.regionals.first()))
        }

        if (data.university.hashTag == null) {
            println("HASTAG MISSED " + data.id)
        } else if (data.university.hashTag.length > 19) {
            println("very long hashtag " + data.university.hashTag + " id=" + data.id)
        }

        //set colors
        if (color != null) {
            replaced = replaced.replace("{mainColor}", color.hex).replace("{fontColor}", color.fontColor)
        }
        File(path, "${data.id}_main.svg").writeText(replaced, Charsets.UTF_8)
    }

    fun generatePersonSVG(id: Int, person: Person, path: String, role: String, color: Color) {
        var xCoord = 158
        var replaced = if (role == "coach") {
            xCoord = 240
            File("src/main/resources/svg/coach.svg").readText(Charsets.UTF_8)
                .replace("{Name}", person.name)
        } else {
            File("src/main/resources/svg/contestant.svg").readText(Charsets.UTF_8)
                .replace("{Name}", person.name)
        }

        if (person.name.length > 21 && person.achievements!!.isNotEmpty()) {
            println("Check by hands long name=" + person.name + ", teamId="+ id + "_" + role)
        }

        var rating = ""
        if (person.cfRating != null) {
            rating += placeRating(person.cfRating.toString(), xCoord, "CF")
        }

        var longValue = false
        for ((index, value) in person.achievements!!.withIndex()) {
            replaced = replaced.replace("{ach${index+1}}", value.achievement)
            if (value.achievement.length >= 38) {
                longValue = true
            }
        }
        if (longValue && person.achievements.size > 4) {
            println("Check by hands long achievement string:" + id + "_" + role)
        }

        for (i in 1..8) {
            replaced = replaced.replace("{ach$i}", "")
        }

        //set colors
        replaced = replaced
            .replace("{Logo}", base64Logo(logo, id, defaultLogo))
            .replace("{Rating Circles}", rating)
            .replace("{mainColor}", color.hex).replace("{fontColor}", color.fontColor)

        File(path, "${id}_" + role + ".svg").writeText(replaced, Charsets.UTF_8)
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

    fun generateFinalsSVG(data: Data, path: String, color: Color) {
        var replaced = File("src/main/resources/svg/finals.svg").readText(Charsets.UTF_8)
            .replace("{Logo}", base64Logo(logo, data.id, defaultLogo))

        if (data.university.appYears != null) {
            replaced = replaced.replace("{F}", data.university.appYears.size.toString())
        } else {
            replaced = replaced.replace("{F}", "1")
        }

        replaced = addMedals(replaced, data.university)

        if (universityHasMedals(data.university)) {
            var startsFrom = 455.29
            replaced = addCups(replaced, data.university, startsFrom)

            if (data.university.winYears!= null) {
                val size = data.university.winYears.size
                startsFrom += if (size >= 5) size * 50 else size * 70
            }

            replaced = addRC(replaced, data.university, startsFrom + 20)
        }

        //set colors
        replaced = replaced.replace("{mainColor}", color.hex).replace("{fontColor}", color.fontColor)
        File(path, "${data.id}_finals.svg").writeText(replaced, Charsets.UTF_8)
    }

    private fun parseTeamsColors(file: File): List<Color> {
        val reader = JsonReader(FileReader(file))
        val teams: Array<TeamColor> = Gson().fromJson(reader, Array<TeamColor>::class.java)


        return teams.map {
            colors[it.colorName]!!
        }
    }

    private fun universityHasMedals(university: University) =
        university.goldYears?.isNotEmpty() == true || university.silverYears?.isNotEmpty() == true || university.bronzeYears?.isNotEmpty() == true

    private fun splitNameTwoPart(fullName: String): Pair<String, String> {
        val s = fullName.substring(0, min(fullName.length, 35)).substringBeforeLast(" ")
        return Pair(s.trim(), fullName.substring(s.length, fullName.length).trim())
    }
}

