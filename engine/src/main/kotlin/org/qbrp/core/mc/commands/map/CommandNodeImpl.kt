package org.qbrp.core.mc.commands.map

import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder.literal
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import net.minecraft.server.command.ServerCommandSource
import org.qbrp.core.mc.commands.TypeValidator

data class CommandNodeImpl(
    override var name: String,
    override val subCommands: MutableList<CommandNode> = mutableListOf(),
    override val arguments: MutableList<ArgumentNode> = mutableListOf(),
    override var execute: Executor? = null,
) : CommandNode {
    override fun getLiteral(): LiteralArgumentBuilder<ServerCommandSource> {
        val commandLiteral = literal<ServerCommandSource>(name)

        // Построение цепочки аргументов
        var lastArgumentBuilder: ArgumentBuilder<ServerCommandSource, *>? = null

        // Строим цепочку аргументов
        arguments.reversed().forEachIndexed { index, argument ->
            if (!argument.sub) {
                val argumentType = TypeValidator.getArgumentType(argument.type)
                val requiredArgumentBuilder = RequiredArgumentBuilder.argument<ServerCommandSource, Any>(
                    argument.name,
                    argumentType as ArgumentType<Any>
                ).apply {
                    argument.provider?.let { suggests(it) }
                }

                // Если это последний аргумент, добавляем execute
                if (index == 0 && execute != null) {
                    requiredArgumentBuilder.executes { context ->
                        execute?.execute(context)
                        1
                    }
                }

                // Связываем текущий аргумент с предыдущими
                if (lastArgumentBuilder != null) {
                    requiredArgumentBuilder.then(lastArgumentBuilder)
                }

                lastArgumentBuilder = requiredArgumentBuilder
            }
        }

        // Добавляем подкоманды
        subCommands.forEach { subCommand ->
            if (lastArgumentBuilder != null) {
                // Если есть аргументы, добавляем подкоманды к последнему аргументу
                (lastArgumentBuilder as RequiredArgumentBuilder<ServerCommandSource, *>).then(subCommand.getLiteral())
            } else {
                // Если аргументов нет, добавляем подкоманды напрямую к корню
                commandLiteral.then(subCommand.getLiteral())
            }
        }

        // Добавляем цепочку аргументов к корню команды
        if (lastArgumentBuilder != null) {
            commandLiteral.then(lastArgumentBuilder)
        }

        // Если нет аргументов, добавляем execute на корень
        if (lastArgumentBuilder == null && execute != null) {
            commandLiteral.executes { context ->
                execute?.execute(context)
                1
            }
        }

        return commandLiteral
    }

}
