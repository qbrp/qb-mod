package org.qbrp.core.mc.commands.annotations

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class SubCommand(val name: String = "AUTO")