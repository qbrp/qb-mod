//package org.qbrp.core.mc.items.components.meta
//
//import com.fasterxml.jackson.annotation.JsonIgnore
//import net.minecraft.entity.player.PlayerEntity
//import net.minecraft.item.ItemStack
//import net.minecraft.nbt.NbtCompound
//import net.minecraft.nbt.NbtList
//import net.minecraft.nbt.NbtString
//import net.minecraft.text.Text
//import org.qbrp.core.components.ItemComponent
//import org.qbrp.engine.items_old.model.ItemState
//import org.qbrp.system.utils.format.Format.asMiniMessage
//import org.qbrp.system.utils.format.Format.miniMessage
//
//class ItemDisplay(private val name: String, private var description: String, var placeholders: MutableMap<String, String> = mutableMapOf()) : ItemComponent() {
//    @JsonIgnore
//    private var textName: Text = "<italic:false>$name".asMiniMessage()
//
//    fun placeholder(name: String, value: String) {
//        placeholders[name] = value
//    }
//
//    override fun onTick(item: ItemStack, player: PlayerEntity, state: ItemState) {
//        item.setLore(formatDescription())
//    }
//
//    override fun onActivate(item: ItemStack, player: PlayerEntity, state: ItemState) {
//        item.setCustomName(textName)
//    }
//
//    private fun formatDescription(): List<String> =
//        description.replacePlaceholders(placeholders).split("\n")
//
//    companion object {
//        private fun String.replacePlaceholders(placeholders: Map<String, String>): String {
//            return if (!placeholders.isEmpty()) placeholders.entries.fold(this) { acc, (key, value) -> acc.replace("{$key}", value) }
//            else ""
//        }
//
//        fun ItemStack.setLore(lore: List<String>) {
//            val displayTag: NbtCompound = this.getOrCreateSubNbt("display")
//            val loreList = NbtList().apply {
//                lore.forEach { NbtString.of(it.miniMessage()) }
//            }
//            displayTag.put("lore", loreList)
//        }
//    }
//}
