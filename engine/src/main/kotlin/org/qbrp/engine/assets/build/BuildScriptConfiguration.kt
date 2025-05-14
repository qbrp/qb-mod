package org.qbrp.engine.assets.build

import kotlin.script.experimental.api.ScriptCompilationConfiguration
import kotlin.script.experimental.api.baseClass
import kotlin.script.experimental.api.defaultImports
import kotlin.script.experimental.api.providedProperties
import kotlin.script.experimental.jvm.dependenciesFromCurrentContext
import kotlin.script.experimental.jvm.jvm

object BuildScriptConfiguration: ScriptCompilationConfiguration({
    jvm {
        dependenciesFromCurrentContext(wholeClasspath = true)
    }
    providedProperties(
        "build" to BuildApi::class
    )
    baseClass(BuildScript::class)
}) {
    private fun readResolve(): Any = BuildScriptConfiguration
}