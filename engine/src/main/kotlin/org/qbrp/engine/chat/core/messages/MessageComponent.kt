package org.qbrp.engine.chat.core.messages

import org.qbrp.system.networking.messages.components.Component
import org.qbrp.system.networking.messages.components.ComponentTypeFactory
import org.qbrp.system.networking.messages.types.BilateralContent
import org.qbrp.system.networking.messages.types.SendContent
import org.qbrp.system.networking.messages.types.StringContent
import kotlin.reflect.full.primaryConstructor

data class MessageComponent(val name: String, val value: Any) {
    fun build(): Component {
        // SendContent, так как мы принимаем лишь примитивные типы, а значит и Bilateral
        val data = (ComponentTypeFactory().buildComponentType(value::class.java) as SendContent).apply { setData(value) }
        return Component(name, data)
    }
}