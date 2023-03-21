package com.vvolochay

import java.io.File
import java.nio.file.Path
import kotlin.io.path.createDirectories


val outputDir = Path.of("build/generated").createDirectories().toString()

fun main(args: Array<String>) {
    if (args.isEmpty()) {
        println("Needs to set file with info about contestants")
        return
    }

    val dataFile = File(args[0])
    if (dataFile.isDirectory) {
        // generate wf achievements from json files
        FullAchievementsGen().run(dataFile)
    } else {
        // generate base achievement from txt file
        BaseAchievementsGen().run(dataFile)

    }
    println("All generated files added to folder $outputDir")
}