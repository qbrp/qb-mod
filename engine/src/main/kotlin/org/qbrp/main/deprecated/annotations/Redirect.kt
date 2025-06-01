package org.qbrp.deprecated.annotations

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Redirect(val names: Array<String>)
