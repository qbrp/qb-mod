package org.qbrp.main.core.modules

import net.fabricmc.api.EnvType

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class Autoload(val priority: Int = LoadPriority.MODULE, val env: EnvType = EnvType.SERVER, val both: Boolean = false)