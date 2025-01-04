package org.qbrp.core.game.items

import net.minecraft.item.Item
import net.minecraft.util.Identifier
import org.imperial_hell.qbrp.client.Items.BaseItemType
import org.qbrp.core.ServerCore.Companion.MOD_ID
import org.qbrp.core.resources.structure.integrated.Parents

class BaseItem(val name: String,
               val type: Item = BaseItemType(Item.Settings()),
               val modelType: Parents = Parents.GENERATED
) {
    val identifier = Identifier(MOD_ID, name)
}