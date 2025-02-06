package org.qbrp.core.game.commands.annotations

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class SubCommand(val name: String = "AUTO")