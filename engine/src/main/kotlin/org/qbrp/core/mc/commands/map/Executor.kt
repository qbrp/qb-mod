package org.qbrp.core.mc.commands.map

import PermissionManager.hasPermission
import com.mojang.brigadier.context.CommandContext
import net.minecraft.server.command.ServerCommandSource
import org.qbrp.core.mc.commands.Deps
import org.qbrp.core.mc.commands.TypeValidator
import org.qbrp.core.mc.commands.annotations.Arg
import org.qbrp.system.utils.format.Format.formatMinecraft
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method

data class Executor(val method: Method,
                    val commandClazz: Class<*>,
                    val deps: Deps,
                    var printError: Boolean = false,
                    val permission: String = "",
                    val operatorLevel: Int = 0) {
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

                TypeValidator.getArgumentValue<Any>(if (argAnnotation.type == "") parameter.type.simpleName else argAnnotation.type, context, parameter.name)
            }.toTypedArray()
        }

        val instance = constructor.newInstance(*args)
        try {
            if (checkPermission(context)) {
                method.invoke(instance, context, deps)
            } else {
                context.source.sendError("Недостаточно прав".formatMinecraft())
            }
        } catch (e: InvocationTargetException) {
            context.source.sendError("Ошибка: ${e.message}".formatMinecraft())
            if (printError) { e.printStackTrace() }
        }
    }

    fun checkPermission(context: CommandContext<ServerCommandSource>): Boolean {
        if (context.source.player?.hasPermissionLevel(4) == true) return true
        return context.source.player?.hasPermission(permission) != false && context.source.player?.hasPermissionLevel(operatorLevel) == true
    }
}