package org.qbrp.main.core.util.networking.messages.types
import net.minecraft.network.PacketByteBuf
import org.joml.Quaternionf

class QuaternionContent(var quat: Quaternionf? = null) : BilateralContent() {

    override fun toString(): String = "$quat"
    override fun getData(): Quaternionf = quat!!
    override fun setData(data: Any) { quat = data as Quaternionf}
    fun setData(data: Quaternionf) { quat = data }

    override fun convert(buf: PacketByteBuf): QuaternionContent { quat = buf.readQuaternionf(); return this }
    override fun write(buf: PacketByteBuf): PacketByteBuf {
        return buf.apply { writeQuaternionf(quat) }
    }
}