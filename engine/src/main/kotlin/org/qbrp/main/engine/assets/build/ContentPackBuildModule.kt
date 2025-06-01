package org.qbrp.main.engine.assets.build

import org.qbrp.main.engine.Engine
import org.qbrp.main.engine.assets.resourcepack.versioning.ResourcePackVersionsAPI
import org.qbrp.main.core.modules.QbModule
import org.qbrp.main.core.utils.log.LoggerUtil
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
        private val LOGGER = LoggerUtil.get("build")
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