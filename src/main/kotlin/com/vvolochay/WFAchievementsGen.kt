package com.vvolochay

import com.google.gson.Gson
import com.google.gson.stream.JsonReader
import java.io.File
import java.io.FileReader

data class Data(
    val id: Int,
    val university: University,
    val team: Team,
    val coach: Person,
    val contestants: List<Person>
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

    private var fromY: Int = 40
    private var text: String = "<text class=\"box\" x=\"140\" y=\"{FromY}\" >{part}</text>"

    private lateinit var output: String
    private lateinit var logo: File

    override fun run(filename: File, logo: File, template: File, outputDir: String, result: Boolean) {
        val teamData = parseJsons(filename)
        this.output = outputDir
        this.logo = logo

        for (data in teamData) {

            generateMainSVG(data, outputDir)
//            generatePersonSVG(data, data.coach, "coach")
//            for (i in 0 until data.contestants.size) {
//                generatePersonSVG(data, data.contestants[i], "contestant_$i")
//            }
        }
    }

    fun generateMainSVG(data: Data, path: String) {
        val svgText: String = File("src/main/resources/svg/main_1_1.svg").readText(Charsets.UTF_8)

        var replaced = if (data.university.fullName.length >= 55) {
            svgText.replace("font-size: 24", "font-size: 20")
                .replace("{University Name}", splitNameBySpace(data.university.fullName, 22))
        } else {
            svgText.replace("{University Name}", splitNameBySpace(data.university.fullName))
        }

        replaced = replaced
            .replace("{Logo}", base64Logo(if (logo.isDirectory) File(logo.path + "/" + data.id + ".jpg") else logo))
            .replace("{ShortTeamName}", data.university.shortName)
            .replace("{Region}", data.university.region)
            .replace("{HashTag}", data.university.hashTag ?: "")
            .replace("&", "&amp;") // fix escaping symbols

        File(path, "${data.id}_main.svg").writeText(replaced, Charsets.UTF_8)
    }

    private fun parseJsons(dataFiles: File): List<Data> {
        val teamsData = mutableListOf<Data>()
        dataFiles.listFiles()?.forEach {
            val reader = JsonReader(FileReader(it))
            val data: Data = Gson().fromJson(reader, Data::class.java)
            teamsData.add(data)
        }
        return teamsData
    }

    private fun splitNameBySpace(fullName: String, maxLen: Int = 16): String {
        val nameParts = mutableListOf<String>()
        var name = fullName
        while (name.length > maxLen && name.contains(" ")) {
            val s = name.substring(0, maxLen).substringBeforeLast(" ")
            nameParts.add(s)
            name = name.removeRange(0, s.length).trim()
        }
        nameParts.add(name)

        nameParts.mapIndexed { index, s ->
            nameParts[index] = text.replace("{part}", s).replace("{FromY}", (fromY + index * 30).toString())
        }
        return nameParts.joinToString("\n")
    }
}

