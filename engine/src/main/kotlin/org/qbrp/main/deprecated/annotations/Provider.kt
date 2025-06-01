package org.qbrp.deprecated.annotations

import kotlin.reflect.KClass

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class Provider(val clazz: KClass<*>)
