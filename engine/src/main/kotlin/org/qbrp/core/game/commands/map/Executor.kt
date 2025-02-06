package org.qbrp.core.game.commands.map

import com.mojang.brigadier.context.CommandContext
import net.minecraft.server.command.ServerCommandSource
import org.qbrp.core.game.commands.Deps
import org.qbrp.core.game.commands.TypeValidator
import org.qbrp.core.game.commands.annotations.Arg
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method

data class Executor(val method: Method, val commandClazz: Class<*>, val deps: Deps, var printError: Boolean = false) {
    fun execute(context: CommandContext<ServerCommandSource>) {
        val constructor = commandClazz.constructors.firstOrNull()
            ?: throw IllegalStateException("Класс ${commandClazz.simpleName} не имеет конструктора")

        // Если конструктор не имеет аргументов, создаем инстанс без аргументов
        val args = if (constructor.parameters.isEmpty()) {
            emptyArray()
        } else {
            constructor.parameters.map { parameter ->
                val argAnnotation = parameter.getAnnotation(Arg::class.java)
                    ?: throw IllegalArgumentException("Параметр ${parameter.name} не имеет аннотации @Arg")

                // Получение значения аргумента
                TypeValidator.getArgumentValue<Any>(argAnnotation.type, context, parameter.name)
            }.toTypedArray()
        }

        val instance = constructor.newInstance(*args)
        try {
            method.invoke(instance, context, deps)
        } catch (e: InvocationTargetException) {
            if (printError) { e.printStackTrace() }
        }
    }
}