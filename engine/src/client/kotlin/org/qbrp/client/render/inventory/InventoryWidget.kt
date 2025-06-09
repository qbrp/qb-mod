package org.qbrp.client.render.inventory

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.item.ItemStack
import net.minecraft.text.Text
import org.qbrp.client.engine.inventory.HeldItem
import org.qbrp.client.engine.inventory.model.ActionHandler
import org.qbrp.client.engine.inventory.model.ItemProvider
import org.qbrp.client.render.hud.InventoryManager
import kotlin.math.*

class InventoryWidget(
    private val title: Text,
    private val handler: ActionHandler,
    private val itemProvider: ItemProvider,
    private val heldItem: HeldItem,
    val containerId: String,
) {
    companion object {
        private const val MAX_COLUMNS = 9
        private const val SLOT_SIZE = 18
        private const val PANEL_HEIGHT = 12    // чуть выше для текста
        private const val PADDING = 4          // внутренний отступ
        private const val SHADOW_OFFSET = 3
        private const val HOVER_SCALE = 1.1f
        private const val ANIM_SPEED = 0.15f   // скорость анимации
    }

    // кэш стеков и сетка
    private var itemsCache: List<ItemStack> = emptyList()
    private var slotXs = IntArray(0)
    private var slotYs = IntArray(0)
    private var rows = 1
    private var columns = 1
    private var hoveredIndex = -1

    // анимация hover: прогресс от 0f до 1f
    private var hoverProgress = FloatArray(0)

    // смещение всего виджета
    private var offsetX = 0
    private var offsetY = 0

    // для перетаскивания
    private var dragging = false
    private var dragStartMouseX = 0
    private var dragStartMouseY = 0
    private var dragStartOffsetX = 0
    private var dragStartOffsetY = 0

    private val width
        get() =  columns * SLOT_SIZE + PADDING
    private val height
        get() =  rows * SLOT_SIZE + PANEL_HEIGHT + PADDING

    fun isMouseHovered(mouseX: Int, mouseY: Int): Boolean {
        return mouseX >= offsetX && mouseX <= offsetX + width && mouseY >= offsetY && mouseY <= offsetY + height
    }

    fun init(client: MinecraftClient) {
        updateItems()  // инициализируем и hoverProgress
        computeGrid(client)
        offsetX = client.window.scaledWidth / 2 - (columns * SLOT_SIZE) / 2
        offsetY = client.window.scaledHeight / 2 - (rows * SLOT_SIZE + PANEL_HEIGHT + PADDING * 2) / 2
    }

    private fun updateItems() {
        val newItems = itemProvider.provideStacks().toMutableList().apply { add(ItemStack.EMPTY) }
        if (newItems.size != itemsCache.size) {
            itemsCache = newItems
            hoverProgress = FloatArray(itemsCache.size)
        } else {
            itemsCache = newItems
        }
    }

    private fun computeGrid(client: MinecraftClient) {
        val total = itemsCache.size
        columns = min(total, MAX_COLUMNS)
        rows = (total + columns - 1) / columns
        slotXs = IntArray(total) { i -> offsetX + PADDING + (i % columns) * SLOT_SIZE }
        slotYs = IntArray(total) { i -> offsetY + PANEL_HEIGHT + PADDING + (i / columns) * SLOT_SIZE }
    }

    fun render(context: DrawContext, client: MinecraftClient?, mouseX: Int, mouseY: Int) {
        if (client == null || client.window.scaledWidth == 0 || client.window.scaledHeight == 0) return

        updateItems()
        computeGrid(client)

        // фон с тенью
        val bgX0 = offsetX - SHADOW_OFFSET
        val bgY0 = offsetY - SHADOW_OFFSET
        val bgX1 = offsetX + columns * SLOT_SIZE + PADDING * 2 + SHADOW_OFFSET
        val bgY1 = offsetY + rows * SLOT_SIZE + PANEL_HEIGHT + PADDING * 2 + SHADOW_OFFSET
        //context.fill(bgX0, bgY0, bgX1, bgY1, 0x80000000.toInt()) // тень
        context.fill(offsetX, offsetY, bgX1 - SHADOW_OFFSET, bgY1 - SHADOW_OFFSET, 0xCC222222.toInt()) // фон панели

        // заголовок
        context.drawText(client.textRenderer, title, offsetX + PADDING, offsetY + PADDING, 0xFFFFFF, false)

        // отрисовка слотов с анимацией hover
        hoveredIndex = -1
        for (i in itemsCache.indices) {
            val x = slotXs[i]
            val y = slotYs[i]
            val stack = itemsCache[i]

            // определяем hover
            val isHover = mouseX in x until x + SLOT_SIZE && mouseY in y until y + SLOT_SIZE
            hoverProgress[i] = (hoverProgress[i] + if (isHover) ANIM_SPEED else -ANIM_SPEED).coerceIn(0f, 1f)
            if (isHover) hoveredIndex = i

            // фон слота
            context.fill(x - 1, y - 1, x + SLOT_SIZE + 1, y + SLOT_SIZE + 1, 0xFF555555.toInt())
            context.fill(x, y, x + SLOT_SIZE, y + SLOT_SIZE, 0xFF333333.toInt())

            // overlay при hover
            if (hoverProgress[i] > 0f) {
                context.fill(
                    x,
                    y,
                    x + SLOT_SIZE,
                    y + SLOT_SIZE,
                    ((hoverProgress[i] * 0x40).toInt() shl 24) or 0xFFFFFF
                )
            }

            // рассчитываем масштаб
            val scale = 1f + (HOVER_SCALE - 1f) * hoverProgress[i]
            val offset = ((SLOT_SIZE - SLOT_SIZE * scale) / 2)
            val drawX = (x + offset).toInt()
            val drawY = (y + offset).toInt()

            // отрисовка предмета
            if (!stack.isEmpty) {
                context.drawItem(stack, drawX + 1, drawY + 1)
                context.drawItemInSlot(client.textRenderer, stack, drawX + 1, drawY + 1)
            }
        }

        // тултип
        if (hoveredIndex != -1) {
            val hoveredItem = itemsCache[hoveredIndex]
            if (!hoveredItem.isEmpty) {
                context.drawItemTooltip(client.textRenderer, hoveredItem, mouseX, mouseY)
            }
        }
    }

    fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        val mx = mouseX.toInt()
        val my = mouseY.toInt()
        if (button == 0 && mx in offsetX until offsetX + columns * SLOT_SIZE + PADDING * 2 &&
            my in offsetY until offsetY + PANEL_HEIGHT + PADDING * 2) {
            dragging = true
            dragStartMouseX = mx
            dragStartMouseY = my
            dragStartOffsetX = offsetX
            dragStartOffsetY = offsetY
            return true
        }
        if (hoveredIndex != -1) {
            val slotStack = itemsCache[hoveredIndex]
            when (button) {
                0 -> {
                    when {
                        heldItem.isEmpty && !slotStack.isEmpty -> handler.takeItem(hoveredIndex, containerId)
                        !heldItem.isEmpty && slotStack.isEmpty -> handler.putItem(hoveredIndex, containerId)
                        !heldItem.isEmpty && !slotStack.isEmpty -> handler.swapItem(hoveredIndex, containerId)
                    }
                }
                1 -> if (!heldItem.isEmpty && !slotStack.isEmpty) handler.swapItem(hoveredIndex, containerId)
            }
            return true
        }
        return false
    }

    fun mouseDragged(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (dragging && button == 0) {
            val mx = mouseX.toInt()
            val my = mouseY.toInt()
            offsetX = dragStartOffsetX + (mx - dragStartMouseX)
            offsetY = dragStartOffsetY + (my - dragStartMouseY)
            return true
        }
        return false
    }

    fun mouseReleased(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (button == 0 && dragging) {
            dragging = false
            return true
        }
        return false
    }
}
