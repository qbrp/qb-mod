package org.qbrp.main.engine.items.physics.debug

import org.qbrp.main.core.components.StateUnavailableComponent
import org.qbrp.main.engine.players.model.ServerPlayerObject
import org.qbrp.main.engine.items.model.ServerItemObject
import org.qbrp.main.engine.text.components.MiniMessageTextComponent
import org.qbrp.main.engine.text.components.TextComponent
import org.qbrp.main.engine.text.emptyText
import org.qbrp.main.engine.items.display.PlainTextTooltip

class PhysicDebug : StateUnavailableComponent, PlainTextTooltip {
    override fun provide(
        player: ServerPlayerObject,
        item: ServerItemObject,
    ): TextComponent {
        val agent = item.agent
        if (agent == null) { return emptyText() }
        val temperature = "Температура: <blue>${agent.temperature} K (${agent.temperature - 273.15} C)</blue>"
        val mass = "Масса: <blue>${agent.mass} гр</blue>"
        val volume = "Объем: <blue>${agent.volume} см3</blue>"
        val base = "$temperature<newline>$mass<newline>$volume<newline>"
        val materials: String = agent.materials
            .joinToString(separator = "<newline>") { m ->
                val pct = m.proportion * 100
                val info = m
                    .filterIsInstance<DebugInfo>()
                    .joinToString("<newline>") { it.getInfo() }
                "<green>– ${m.name} (${pct}%)</green><newline>" +
                "Плотность: <green>${m.density}</green><newline>" +
                "Молярная масса: <green>${m.molarMass}</green><newline>" +
                info
            }
        return MiniMessageTextComponent("$base$materials")
    }
}