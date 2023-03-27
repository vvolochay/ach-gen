package com.vvolochay

import com.google.gson.Gson
import com.google.gson.stream.JsonReader
import java.io.File
import java.io.FileReader
import java.nio.file.Path
import kotlin.io.path.createDirectories

data class Data(val id: Int, val university: University, val team: Team, val coach: Person, val contestants: List<Person>)

data class Team(val name: String, val regionals: List<String>)

data class University(val fullName: String, val shortName: String, val region: String, val hashTag: String?, val url: String,
                      val appYears: List<Int>? = null, val winYears: List<Int>? = null, val goldYears: List<Int>? = null,
                      val silverYears: List<Int>? = null, val bronzeYears: List<Int>? = null, val regYears: List<Int>? = null)

data class Person(val name: String, val altNames: List<String>, val tcHandle: String? = null, val tcRating: Int? = null,
                  val cfHandle: String? = null, val cfRating: Int? = null, val twitterHandle: String? = null,
                  val achievements: List<Achievement>? = null)

data class Achievement(val achievement: String, val priority: Int)


class FullAchievementsGen : Generator() {

    private lateinit var output: String
    private lateinit var logo: File

    override fun run(filename: File, logo: File, template: File, outputDir: String, result: Boolean) {
        val teamData = parseJsons(filename)
        this.output = outputDir
        this.logo = logo

        for (data in teamData) {
            generateMainSVG(data)
//            generatePersonSVG(data, data.coach, "coach")
//            for (i in 0 until data.contestants.size) {
//                generatePersonSVG(data, data.contestants[i], "contestant_$i")
//            }
        }
    }

    fun generateMainSVG(data: Data) {
        val svgText: String = if (data.university.fullName.length <= 45) {
            File("src/main/resources/base/main.svg").readText(Charsets.UTF_8)
                .replace("{FullUniversityName}", data.university.fullName)
        } else {
            val firstSubstring = data.university.fullName.substring(0, 45).substringBeforeLast(" ")
            File("src/main/resources/base/main2.svg").readText(Charsets.UTF_8)
                .replace("{FullUniversityName_1}", firstSubstring)
                .replace("{FullUniversityName_2}", data.university.fullName.substring(firstSubstring.length, data.university.fullName.length).trim())
        }

        val replaced = svgText.replace("{Logo}", base64Logo(if (logo.isDirectory) File(logo.path + "/" + data.id + ".jpg") else logo))
            .replace("{ShortTeamName}", data.university.shortName)
            .replace("{Region}", data.university.region)
            .replace("{HashTag}", data.university.hashTag ?: "")
            .replace(
                "{FinalsCounter}",
                if (data.university.appYears == null || data.university.appYears.size == 1) " 1 FINAL"
                else data.university.appYears.size.toString() + " FINALS"
            )

        Path.of("$output/main").createDirectories().toString()
        File("$output/main/" + data.id.toString() + "_main.svg").writeText(replaced, Charsets.UTF_8)
    }

    fun parseJsons(dataFiles: File): List<Data> {
        val teamsData = mutableListOf<Data>()
        dataFiles.listFiles()?.forEach {
            val reader = JsonReader(FileReader(it))
            val data: Data = Gson().fromJson(reader, Data::class.java)
            teamsData.add(data)
        }
        return teamsData
    }
}

