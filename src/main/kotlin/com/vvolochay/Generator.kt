package com.vvolochay

import java.io.File

abstract class Generator {

    open fun run (filename: File, logo: File, template: File, outputDir: String, result: Boolean) {}
}