package org.qbrp.core.mc.commands.annotations

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Execute(val permission: String = "", val operatorLevel: Int = 0)
