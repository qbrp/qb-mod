package org.qbrp.deprecated.annotations

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class SubCommand(val name: String = "AUTO")