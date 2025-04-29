package org.qbrp.core.mc.commands.annotations

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Redirect(val names: Array<String>)
