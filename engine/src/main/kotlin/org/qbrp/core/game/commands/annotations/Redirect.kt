package org.qbrp.core.game.commands.annotations

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Redirect(val names: Array<String>)
