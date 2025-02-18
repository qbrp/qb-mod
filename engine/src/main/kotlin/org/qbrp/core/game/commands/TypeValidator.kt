package org.qbrp.core.game.commands
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.arguments.BoolArgumentType
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import net.minecraft.server.command.ServerCommandSource

object TypeValidator {
    // Метод для определения типа аргумента
    fun getArgumentType(type: String): ArgumentType<*> {
        return when (type.lowercase()) {
            "string" -> StringArgumentType.string()
            "word" -> StringArgumentType.word()
            "integer" -> IntegerArgumentType.integer()
            "int" -> IntegerArgumentType.integer()
            "boolean" -> BoolArgumentType.bool()
            else -> throw IllegalArgumentException("Unsupported argument type: $type")
        }
    }

    // Метод для получения значения аргумента из контекста
    fun <T> getArgumentValue(type: String, context: CommandContext<ServerCommandSource>, name: String): T {
        return when (type.lowercase()) {
            "string" -> StringArgumentType.getString(context, name) as T
            "integer" -> IntegerArgumentType.getInteger(context, name) as T
            "int" -> IntegerArgumentType.getInteger(context, name) as T
            "boolean" -> BoolArgumentType.getBool(context, name) as T
            "word" -> StringArgumentType.getString(context, name) as T
            else -> throw IllegalArgumentException("Unsupported argument type: $type")
        }
    }
}
