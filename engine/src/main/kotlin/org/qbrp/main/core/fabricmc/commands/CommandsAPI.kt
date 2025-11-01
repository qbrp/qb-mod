package org.qbrp.main.core.fabricmc.commands

import com.mojang.brigadier.arguments.BoolArgumentType
import com.mojang.brigadier.arguments.DoubleArgumentType
import com.mojang.brigadier.arguments.FloatArgumentType
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.CommandSource
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

interface CommandsAPI {
    fun add(commands: List<CommandRegistryEntry>)
    fun add(command: CommandRegistryEntry)
}

fun KoinComponent.registerCommand(command: CommandRegistryEntry) {
    get<CommandsAPI>().add(command)
}

fun <T: CommandSource> CommandContext<T>.getString(argument: String): String {
    return StringArgumentType.getString(this, argument)
}

fun <T : CommandSource> CommandContext<T>.getInt(argument: String): Int {
    return IntegerArgumentType.getInteger(this, argument)
}

fun <T : CommandSource> CommandContext<T>.getFloat(argument: String): Float {
    return FloatArgumentType.getFloat(this, argument)
}

fun <T : CommandSource> CommandContext<T>.getDouble(argument: String): Double {
    return DoubleArgumentType.getDouble(this, argument)
}

fun <T : CommandSource> CommandContext<T>.getBoolean(argument: String): Boolean {
    return BoolArgumentType.getBool(this, argument)
}