package org.qbrp.main.engine.items.model

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerBlockEntityEvents
import net.minecraft.entity.Entity
import net.minecraft.entity.ItemEntity
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import org.qbrp.main.core.game.loop.Tick
import java.util.concurrent.ConcurrentHashMap

class ItemTicker(private val storage: ItemStorage<ServerItemObject>) : Tick<ServerWorld> {
    init {
        ContainerTracker.initialize()
    }

    override fun tick(serverWorld: ServerWorld) {
        val items = collectTickableItems(serverWorld)
        items.forEach { it.tryTick(storage) }
    }

    private fun collectTickableItems(world: ServerWorld): List<ItemWithContext> {
        val result = mutableListOf<ItemWithContext>()

        world.iterateEntities().forEach { entity ->
            when (entity) {
                is ItemEntity -> result += ItemWithContext(entity.stack, entity)
                is Inventory -> result += entity.extractItemStacks()
            }
        }

        ContainerTracker.getAllContainerPositions()
            .mapNotNull { world.getBlockEntity(it) as? Inventory }
            .forEach { inv -> result += inv.extractItemStacks() }

        world.players.forEach { player ->
            result += player.inventory.extractItemStacks(player)
        }

        return result.filter { it.isAbstractItem() }
    }

    private fun Inventory.extractItemStacks(entity: Entity? = null): List<ItemWithContext> =
        (0 until this.size()).map { ItemWithContext(this.getStack(it), entity) }

    private data class ItemWithContext(val stack: ItemStack, val entity: Entity?) {
        fun isAbstractItem(): Boolean =
            !stack.isEmpty && stack.registryEntry.key.get().value.path == "abstract_item"

        fun tryTick(storage: ItemStorage<ServerItemObject>) {
            storage.getObject(stack)?.tick(stack, entity)
        }
    }

    private object ContainerTracker {
        private val containerPositions = ConcurrentHashMap.newKeySet<BlockPos>()
        @Volatile private var initialized = false

        fun initialize() {
            if (initialized) return
            synchronized(this) {
                if (initialized) return

                ServerBlockEntityEvents.BLOCK_ENTITY_LOAD.register(ServerBlockEntityEvents.Load { blockEntity, world ->
                    if (world is ServerWorld && blockEntity is Inventory) {
                        containerPositions.add(blockEntity.pos)
                    }
                })

                ServerBlockEntityEvents.BLOCK_ENTITY_UNLOAD.register(ServerBlockEntityEvents.Unload { blockEntity, world ->
                    if (world is ServerWorld && blockEntity is Inventory) {
                        containerPositions.remove(blockEntity.pos)
                    }
                })

                initialized = true
            }
        }

        fun getAllContainerPositions(): Set<BlockPos> = containerPositions.toSet()
    }
}