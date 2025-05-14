package org.qbrp.engine.assets.build

import kotlin.script.experimental.annotations.KotlinScript

@KotlinScript(
    fileExtension = "kts",
    compilationConfiguration = BuildScriptConfiguration::class
)
abstract class BuildScript(val build: BuildApi) {
}