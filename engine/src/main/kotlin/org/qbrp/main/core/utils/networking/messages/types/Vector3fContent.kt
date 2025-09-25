package org.qbrp.main.core.utils.networking.messages.types
import net.minecraft.network.PacketByteBuf
import org.joml.Quaternionf
import org.joml.Vector3f

class Vector3fContent(var vec: Vector3f? = null) : BilateralContent() {

    override fun toString(): String = "$vec"
    override fun getData(): Vector3f = vec!!
    override fun setData(data: Any) { vec = data as Vector3f}
    fun setData(data: Vector3f) { vec = data }

    override fun convert(buf: PacketByteBuf): Vector3fContent { vec = buf.readVector3f(); return this }
    override fun write(buf: PacketByteBuf): PacketByteBuf {
        return buf.apply { writeVector3f(vec) }
    }
}