package com.vvolochay

import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default
import kotlinx.cli.required
import java.io.File
import java.nio.file.Path
import kotlin.io.path.createDirectories

enum class AchType { Person, WF, Team }

fun main(args: Array<String>) {
    val parser = ArgParser("ach-gen")
    System.setProperty("file.encoding", "UTF8")

    val input by parser.option(ArgType.String, shortName = "i", description = "Input with data: file or directory").required()
    val type by parser.option(ArgType.Choice<AchType>(), shortName = "t", description = "Type of achievements").required()

    val logo by parser.option(ArgType.String, description = "Logo: image file or folder with .JPG files, ICPC logo by default")
        .default("src/main/resources/images/icpc_logo.png")
    val svg by parser.option(ArgType.String, description = "SVG template, ICPC template by default")
        .default("src/main/resources/base/simple1.svg")

    val result by parser.option(ArgType.Boolean, shortName = "r", description = "result").default(false)

    val output by parser.option(ArgType.String, shortName = "o", description = "Output directory").default("build/generated")
    parser.parse(args)

    val gen: Generator = when (type) {
        AchType.Person -> PersonalAchievementsGen()
        AchType.Team -> TeamAchievementsGen()
        AchType.WF -> WFAchievementsGen()
    }

    Path.of(output).createDirectories()
    gen.run(File(input), File(logo), File(svg), output, result)

    println("All generated files added to folder $output")
}