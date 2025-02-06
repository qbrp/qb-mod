package org.qbrp.system.networking.messaging

import net.minecraft.network.PacketByteBuf
import org.qbrp.system.networking.messages.Message
import org.qbrp.system.networking.messages.types.BilateralContent
import org.qbrp.system.networking.messages.types.ReceiveContent
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor

abstract class Receiver<T : ReceiverContext>(val messageId: String, messageTypeClass: KClass<*>,
                    open val callback: (Message, T, Receiver<T>) -> Boolean
) {
    private val constructor = messageTypeClass.primaryConstructor
        ?: throw IllegalArgumentException("Конструктор не найден для класса ${messageTypeClass}")
    protected val createMessageType: (PacketByteBuf) -> ReceiveContent = { buf ->
        val args = constructor.parameters.associate { param ->
            if (param.type.isMarkedNullable) {
                param to null
            }
            else {
                throw IllegalArgumentException("Параметр ${param.name} не nullable, но значение не предоставлено!")
            }
        }
        val content = constructor.callBy(args) as ReceiveContent
        content.convert(buf)
        content
    }

}