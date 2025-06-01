package org.qbrp.main.core.utils.networking.messages.components

import net.minecraft.network.PacketByteBuf
import org.qbrp.main.core.utils.networking.messages.components.readonly.ClusterViewer
import org.qbrp.main.core.utils.networking.messages.components.readonly.ComponentReadonly
import org.qbrp.main.core.utils.networking.messages.types.BilateralContent
import org.qbrp.main.core.utils.log.LoggerUtil

open class Cluster(protected open var components: List<Component>? = emptyList()): BilateralContent() {
    companion object { private val logger = LoggerUtil.get("cluster") }

    override fun toString(): String {
        return "Cluster(components = ${components?.map { it.name }?.joinToString(", ")})"
    }

    override fun write(buf: PacketByteBuf): PacketByteBuf {
        logger.log("Запись кластера с ID $messageId")
        super.write(buf)
        buf.writeInt(components!!.size)
        logger.log("Размер: ${components!!.size} (${buf.writtenBytes.size})")
        components!!.forEach {
            it.write(buf) // Записываем данные компонентов
            logger.log("Запись компонента: ${it.name} (${buf.writtenBytes.size})")
        }
        return buf
    }

    override fun convert(buf: PacketByteBuf): Cluster {
        logger.log("Конвертация кластера (${buf.writtenBytes.size})")
        super.convert(buf)
        logger.log("MessageID: $messageId")
        val componentsCount = buf.readInt()
        logger.log("Кол-во компонентов: $componentsCount")
        val components: MutableList<Component> = mutableListOf()
        repeat(componentsCount) { component ->
            logger.log("Конвертация компонента 1")
            val componentObject = ComponentReadonly(null).convert(buf)
            components.add(Component(componentObject.getData().name, componentObject.getData().content, componentObject.getData().meta))
            logger.log("+ <<${components.last().name}>>: ${components.last().content} (${componentObject.getData().meta})")
        }
        this.components = components
        return this
    }

    open fun getBuilder(): ClusterBuilder {
        return ClusterBuilder().components(components!!)
    }

    override fun getData(): ClusterViewer {
        return ClusterViewer(components!!)
    }

    override fun setData(data: Any) {
        @Suppress("UNCHECKED_CAST")
        components = data as List<Component>
    }
}