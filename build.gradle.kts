plugins {
    application
    kotlin("jvm") version "1.8.10"
}

application {
    mainClass.set("com.vvolochay.MainKt")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.google.code.gson:gson:2.10")
    implementation("org.apache.commons:commons-io:1.3.2")
}

tasks {
    jar {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE

        manifest.attributes["Main-Class"] = "com.vvolochay.MainKt"
        configurations["compileClasspath"].forEach { file: File ->
            from(zipTree(file.absoluteFile))
        }
    }

    named<JavaExec>("run") {
        val args = mutableListOf<String>()
        this.args = args
    }
}
