package org.qbrp.main.core.utils.networking.messages.types
import net.minecraft.item.ItemStack
import net.minecraft.network.PacketByteBuf

class ItemStackContent(var itemStack: ItemStack? = ItemStack.EMPTY) : BilateralContent() {

    override fun toString(): String = "$itemStack"
    override fun getData(): ItemStack = itemStack!!
    override fun setData(data: Any) { itemStack = data as ItemStack}
    fun setData(data: ItemStack) { itemStack = data }

    override fun convert(buf: PacketByteBuf): ItemStackContent { itemStack = buf.readItemStack(); return this }
    override fun write(buf: PacketByteBuf): PacketByteBuf {
        return buf.writeItemStack(itemStack)
    }
}