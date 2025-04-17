package org.qbrp.engine.items.model

import klite.NotFoundException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import net.minecraft.client.item.TooltipContext
import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.text.Text
import net.minecraft.world.World
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.qbrp.core.components.ItemComponent
import org.qbrp.core.resources.content.ItemConfig
import org.qbrp.engine.Engine
import org.qbrp.engine.items.ItemsModule
import org.qbrp.system.utils.format.Format.asMiniMessage
import org.qbrp.system.utils.log.Loggers

class QbItem(): Item(Settings()), KoinComponent {
    fun getItemState(stack: ItemStack): ItemState? {
        val nbtId = stack.nbt!!.getInt("id") ?: return null
        return get<ItemManager>().getItemState(nbtId)
    }
    private val scope = CoroutineScope(Dispatchers.IO)

    fun loadItemState(stack: ItemStack) {
        val nbtId = stack.nbt!!.getInt("id")
        stack.nbt!!.putBoolean("loaded", false)
        scope.async {
            val result = get<ItemManager>().loadFromDatabase(nbtId)
            if (result != null) {
                setActivated(stack, true)
                stack.nbt!!.putBoolean("loaded", true)
            } else {
                deactivateOnNotFoundData(stack)
            }
        }
    }

    fun deactivateOnNotFoundData(stack: ItemStack) {
        setActivated(stack, false)
        logger.error("Предмет деактивирован, т.к. его данные не найдены.")
    }

    fun isActivated(stack: ItemStack): Boolean {
        return stack.nbt!!.getBoolean("activated") == true
    }

    fun isLoaded(stack: ItemStack): Boolean {
        return stack.nbt!!.getBoolean("loaded") == true
    }

    fun setActivated(stack: ItemStack, activated: Boolean) {
        stack.nbt!!.putBoolean("activated", activated)
        if (!activated) {
            stack.setCustomName("<reset><red>Деактивированный предмет".asMiniMessage())
        }
    }

    override fun inventoryTick(stack: ItemStack?, world: World?, entity: Entity?, slot: Int, selected: Boolean) {
        if (world?.isClient ?: return) {

        } else {
            try {
                super.inventoryTick(stack, world, entity, slot, selected)
                if (stack == null || entity == null || entity !is PlayerEntity) return
                if (isActivated(stack) && isLoaded(stack)) {
                    val state = getItemState(stack)
                    if (state != null) {
                        state.tick(stack, entity)
                    } else {
                        loadItemState(stack)
                    }
                }
            } catch (e: Exception) {
                setActivated(stack!!, false)
                val state = getItemState(stack)
                if (state != null) {
                    logger.error("Ошибка обработки тика предмета ${state.name} с идентификатором ${state.id}: ${e.message}")
                    logger.error("- Владелец: ${entity?.name?.string} (${entity?.type?.name?.string}), ${entity?.x}, ${entity?.y}, ${entity?.z}")
                    logger.error("- Компоненты: ${state.components.joinToString("\n")}")
                    e.printStackTrace()
                }
                logger.error("Предмет деактивирован.")
            }
        }
    }

    companion object {
        private val logger = Loggers.get("items")
    }
}