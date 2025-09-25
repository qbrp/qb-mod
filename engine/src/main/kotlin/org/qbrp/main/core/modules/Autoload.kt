package org.qbrp.main.core.modules

import net.fabricmc.api.EnvType

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class Autoload(val priority: Int = Priorities.MODULE, val env: EnvType = EnvType.SERVER, val both: Boolean = false)