package org.qbrp.deprecated

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder.literal
import com.mojang.brigadier.suggestion.SuggestionProvider
import net.minecraft.server.command.ServerCommandSource
import org.qbrp.deprecated.annotations.Arg
import org.qbrp.deprecated.annotations.Command
import org.qbrp.deprecated.annotations.Execute
import org.qbrp.deprecated.annotations.Provider
import org.qbrp.deprecated.annotations.Redirect
import org.qbrp.deprecated.annotations.SubCommand
import org.qbrp.deprecated.map.ArgumentNodeImpl
import org.qbrp.deprecated.map.CommandNode
import org.qbrp.deprecated.map.CommandNodeImpl
import org.qbrp.deprecated.map.Executor

class CommandBuilder {

    private lateinit var mappedCommand: CommandNode
    private lateinit var commandRedirects: List<LiteralArgumentBuilder<ServerCommandSource>>
    private var deps: Deps = Deps()
    private var printErrors = false

    fun buildTree(clazz: Class<*>): CommandBuilder {
        mappedCommand = buildCommandTree(clazz)
        if (clazz.getAnnotation(Redirect::class.java) != null) {
            commandRedirects = clazz.getAnnotation(Redirect::class.java).let {
                it.names.map { name ->
                    literal<ServerCommandSource>(name).redirect(mappedCommand.getLiteral().build())
                }
            }
        }
        return this
    }

    fun getRedirects() = commandRedirects

    fun importDependencies(deps: Deps): CommandBuilder {
        this.deps = deps
        return this
    }

    private fun buildCommandTree(clazz: Class<*>): CommandNode {
        val commandAnnotation = clazz.getAnnotation(Command::class.java)
        val rootCommand = CommandNodeImpl(clazz.simpleName)

        commandAnnotation?.let {
            rootCommand.name = it.name
        }

        clazz.declaredClasses.filter { it.isAnnotationPresent(SubCommand::class.java) }
            .forEach { subCommandClass ->
                val subCommandAnnotation = subCommandClass.getAnnotation(SubCommand::class.java)
                val subCommandNode = buildCommandTree(subCommandClass)
                if (subCommandAnnotation.name == "AUTO") {
                    subCommandNode.name = subCommandClass.simpleName.replaceFirstChar { it.lowercase() }
                } else {
                    subCommandNode.name = subCommandAnnotation.name
                }
                rootCommand.subCommands.add(subCommandNode)
            }

        clazz.declaredConstructors.forEach { constructor ->
            constructor.parameters.forEach { parameter ->
                if (parameter.isAnnotationPresent(Arg::class.java)) {
                    val argAnnotation = parameter.getAnnotation(Arg::class.java)
                    val type = if (argAnnotation.type != "") argAnnotation.type else parameter.type.simpleName
                    val argumentNode = ArgumentNodeImpl(parameter.name, type, argAnnotation.sub)
                    if (parameter.isAnnotationPresent(Provider::class.java)) {
                        val providerAnnotation = parameter.getAnnotation(Provider::class.java)
                        argumentNode.provider = providerAnnotation.clazz::java.get().getConstructor().newInstance() as SuggestionProvider<ServerCommandSource>
                    }
                    rootCommand.arguments.add(argumentNode)
                }
            }
            try {
                clazz.declaredMethods.forEach { method ->
                    if (method.isAnnotationPresent(Execute::class.java)) {
                        val annotation = method.getAnnotation(Execute::class.java)
                        rootCommand.execute = Executor(method, clazz, deps, printErrors, annotation.permission, annotation.operatorLevel)
                    }
                }
            } catch (e: Exception) {
                println("Ошибка при создании экземпляра: ${e.message} (${clazz.simpleName})")
            }
        }

        return rootCommand
    }

    fun printErrors(): CommandBuilder {
        printErrors = true
        return this
    }

    fun print(commandNode: CommandNode = mappedCommand, indent: String = ""): CommandBuilder {
        println("$indent- ${commandNode.name}")

        if (commandNode.arguments.isNotEmpty()) {
            commandNode.arguments.forEach { arg ->
                println("$indent  Arg: ${arg.name} of type ${arg.type} (${arg.sub})")
            }
        }

        commandNode.execute?.let {
            println("$indent  Execute: This command has an execute method.")
        }

        if (commandNode.subCommands.isNotEmpty()) {
            println("$indent  SubCommands:")
            commandNode.subCommands.forEach { subCommand ->
                print(subCommand, "$indent    ") // Рекурсивный вызов с CommandNode
            }
        }
        return this
    }

    fun getCommand() = mappedCommand

}