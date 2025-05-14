package org.qbrp.engine.assets.build

import org.qbrp.engine.Engine
import org.qbrp.engine.assets.resourcepack.versioning.ResourcePackVersionsAPI
import org.qbrp.system.modules.Autoload
import org.qbrp.system.modules.LoadPriority
import org.qbrp.system.modules.QbModule
import org.qbrp.system.utils.log.Loggers
import java.io.File
import kotlin.script.experimental.api.EvaluationResult
import kotlin.script.experimental.api.ResultWithDiagnostics
import kotlin.script.experimental.api.ScriptEvaluationConfiguration
import kotlin.script.experimental.api.providedProperties
import kotlin.script.experimental.host.toScriptSource
import kotlin.script.experimental.jvmhost.BasicJvmScriptingHost
import kotlin.script.experimental.jvmhost.createJvmCompilationConfigurationFromTemplate

//@Autoload(LoadPriority.LOWEST)
class ContentPackBuildModule: QbModule("content-pack-build") {
    companion object {
        val BUILD_SCRIPT: File?
            get() = File("qbrp/build.kts").takeIf { it.exists() }
        private val LOGGER = Loggers.get("build")
    }

    init {
        dependsOn { Engine.isApiAvailable<ResourcePackVersionsAPI>() }
    }

    override fun onEnable() {
        build()
    }

    fun build() {
        BUILD_SCRIPT?.let {
            val result = evalFile(it)
            result.reports.forEach {
                LOGGER.log("[${it.severity}]: ${it.message}")
            }
        }

    }

    fun evalFile(scriptFile: File): ResultWithDiagnostics<EvaluationResult> {
        val compilationConfiguration = createJvmCompilationConfigurationFromTemplate<BuildScript>()
        val evalConfig = ScriptEvaluationConfiguration {
            providedProperties("build" to BuildApi(), "args" to arrayOf<String>(), )
        }
        return BasicJvmScriptingHost().eval(scriptFile.toScriptSource(), compilationConfiguration, evalConfig)
    }
}