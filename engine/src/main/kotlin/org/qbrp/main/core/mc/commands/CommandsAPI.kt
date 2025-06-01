package org.qbrp.main.core.mc.commands

interface CommandsAPI {
    fun add(commands: List<CommandRegistryEntry>)
    fun add(command: CommandRegistryEntry)
}