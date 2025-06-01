package org.qbrp.main.core.utils.log

import org.qbrp.main.engine.Engine
import org.qbrp.main.core.utils.format.ConsoleColors
import org.qbrp.main.core.utils.format.ConsoleColors.color

class InformationMessage(val authors: String, val version: String, val modulesCount: Int) {
    private val components: MutableList<String> = mutableListOf()
    private val logger = LoggerUtil.get("info")

    fun print() {
        getAsciiArt().lines().forEach {
            logger.log(it.color(ConsoleColors.BLUE).color(ConsoleColors.BOLD))
        }
        components.forEach { logger.log(it) }
    }

    private fun getAsciiArt() = """
         _____  ______   ______  _____     ${"|".color(ConsoleColors.PURPLE)} ${authors.color(ConsoleColors.PURPLE)}
        |   __| |_____] |_____/ |_____]    ${"|".color(ConsoleColors.CYAN)} ${version.color(ConsoleColors.CYAN)}
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