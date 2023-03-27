package com.vvolochay

import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default
import kotlinx.cli.required
import java.io.File
import java.nio.file.Path
import kotlin.io.path.createDirectories

fun main(args: Array<String>) {
    val parser = ArgParser("ach-gen")

    val data by parser.option(ArgType.String, shortName = "-d", description = "Input file with data: file or directory").required()
    val logo by parser.option(ArgType.String, shortName = "-l", description = "Logo: image file or folder with .JPG files, ICPC logo by default")
        .default("src/main/resources/images/icpc_logo.png")
    val template by parser.option(ArgType.String, shortName = "-t", description = "SVG template, ICPC template by default, see resources/base/simple1.svg")
        .default("src/main/resources/base/simple1.svg")
    val output by parser.option(ArgType.String, shortName = "-o", description = "Output directory").default("build/generated")
    parser.parse(args)

    val outputDir = Path.of(output).createDirectories().toString();

    val dataFile = File(data)
    if (dataFile.isDirectory) {
        // generate wf achievements from json files
        FullAchievementsGen().run(dataFile, File(logo), File(template), outputDir)
    } else {
        // generate base achievement from txt file
        BaseAchievementsGen().run(dataFile, File(logo), File(template), outputDir)

    }
    println("All generated files added to folder $output")
}