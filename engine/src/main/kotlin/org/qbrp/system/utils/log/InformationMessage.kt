package org.qbrp.system.utils.log

import net.fabricmc.loader.api.FabricLoader
import org.qbrp.engine.Engine
import org.qbrp.system.VersionChecker
import org.qbrp.system.utils.format.ConsoleColors
import org.qbrp.system.utils.format.ConsoleColors.color

class InformationMessage {
    private val components: MutableList<String> = mutableListOf()
    val authors = "lain1wakura"
    val logger = Loggers.get("info")

    fun print() {
        getAsciiArt(Engine.moduleManager.modules.count()).lines().forEach {
            logger.log(it.color(ConsoleColors.BLUE).color(ConsoleColors.BOLD))
        }
        components.forEach { logger.log(it) }
    }

    fun getAsciiArt(modulesCount: Int) = """
         _____  ______   ______  _____     ${"|".color(ConsoleColors.PURPLE)} ${"$authors".color(ConsoleColors.PURPLE)}
        |   __| |_____] |_____/ |_____]    ${"|".color(ConsoleColors.CYAN)} ${VersionChecker.CURRENT_VERSION.toString().color(ConsoleColors.CYAN)}
        |____\| |_____] |    \_ |          ${"|".color(ConsoleColors.GREEN)} ${"Загружено $modulesCount модулей".color(ConsoleColors.GREEN)}
                        
        """.trimIndent()

    fun addErrorComponent(component: String) {
        components.add("- $component".color(ConsoleColors.RED))
    }

    fun addWarnComponent(component: String) {
        components.add("- $component".color(ConsoleColors.ORANGE))
    }

    fun addSuccessComponent(component: String) {
        components.add("<<->> $component")
    }

}