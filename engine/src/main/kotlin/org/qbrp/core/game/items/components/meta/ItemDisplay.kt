package org.qbrp.core.game.items.components.meta

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtList
import net.minecraft.text.Text
import org.qbrp.core.components.ItemComponent
import org.qbrp.system.utils.format.Format.asMiniMessage

class ItemDisplay(private val name: String, private var description: String, private var placeholders: MutableMap<String, String> = mutableMapOf()) : ItemComponent() {
    @Transient private var textName: Text = name.asMiniMessage()

    fun placeholder(name: String, value: String) {
        placeholders[name] = value
    }

    override fun onTick(item: ItemStack, player: PlayerEntity) {
        item.setLore(formatDescription())
    }

    override fun onActivate(item: ItemStack, player: PlayerEntity) {
        item.setCustomName(textName)
    }

    private fun formatDescription(): List<Text> =
        description.replacePlaceholders(placeholders).split("\n").map { it.asMiniMessage() }

    companion object {
        private fun String.replacePlaceholders(placeholders: Map<String, String>): String {
            return if (!placeholders.isEmpty()) placeholders.entries.fold(this) { acc, (key, value) -> acc.replace("{$key}", value) }
            else ""
        }

        fun ItemStack.setLore(lore: List<Text>) {
            val displayTag: NbtCompound = this.getOrCreateSubNbt("display")
            val loreList = NbtList().apply {
                lore.forEach { add(it as NbtElement?) }
            }
            displayTag.put("Lore", loreList)
        }
    }
}
