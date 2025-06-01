package org.qbrp.main.core.game.model.components.methods

import com.fasterxml.jackson.annotation.JsonIgnore
import org.qbrp.main.core.game.model.State
import kotlin.reflect.KFunction
import kotlin.reflect.full.functions
import kotlin.reflect.jvm.isAccessible

class InvokeReference(val method: String, val args: List<Any>) {
    @JsonIgnore val component: String = method.split("/").first()
    @JsonIgnore val function: String = method.split("/").last()
    private var cachedMethod: KFunction<*>? = null

    fun invoke(state: State) {
        val target = state.getComponentByNameOrThrow(component)

        val methodToCall = cachedMethod ?: target::class.functions.find {
            it.name == function
        }?.also {
            it.isAccessible = true
            cachedMethod = it
        } ?: throw RuntimeException("Функция $function компонента $component не найдена")

        // Вызываем с указанием получателя и распаковкой аргументов
        methodToCall.call(target, *args.toTypedArray())
    }

}