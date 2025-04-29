package org.qbrp.core.mc.commands.annotations

import kotlin.reflect.KClass

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class Provider(val clazz: KClass<*>)
