package org.qbrp.core.mc.commands.annotations

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class Arg(val type: String = "", val sub: Boolean = false)
