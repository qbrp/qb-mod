package org.qbrp.core.game.items.groups

import net.minecraft.item.ItemStack
import net.minecraft.item.Items

class ItemGroups {

    val groups: MutableList<Group> = mutableListOf()

    fun add(group: Group): Group { groups.add(group); return group }

    init {
        add(Group("default", "Предметы", ItemStack(Items.FERN)) { group ->
            group.addItems(listOf(
                Items.STONE,
                Items.ANDESITE,
                Items.ANDESITE_SLAB,
                Items.ANDESITE_WALL,
                Items.ANDESITE_STAIRS,
                Items.POLISHED_ANDESITE,
                Items.POLISHED_ANDESITE_SLAB,
                Items.POLISHED_ANDESITE_STAIRS,
                Items.BRICKS,
                Items.OAK_LOG,
                Items.OAK_WOOD,
                Items.OAK_SLAB,
                Items.OAK_DOOR,
                Items.OAK_PLANKS,
                Items.OAK_STAIRS,
                Items.OAK_FENCE,
                Items.OAK_FENCE_GATE,
                Items.OAK_SIGN,
                Items.OAK_TRAPDOOR,
                Items.OAK_LEAVES,
                Items.GRASS,
                Items.TALL_GRASS,
                Items.GRASS_BLOCK,
                Items.PODZOL,
                Items.MYCELIUM,
                Items.DIRT_PATH,
                Items.DIRT,
                Items.COARSE_DIRT,
                Items.ROOTED_DIRT,
                Items.AZALEA ,
                Items.FLOWERING_AZALEA,
                Items.BROWN_MUSHROOM,
                Items.RED_MUSHROOM,
                Items.CRIMSON_FUNGUS,
                Items.WARPED_FUNGUS,
                Items.GRASS,
                Items.FERN,
                Items.DEAD_BUSH,
                Items.DANDELION,
                Items.POPPY,
                Items.BLUE_ORCHID,
                Items.ALLIUM
                )
            )
        })
    }

}