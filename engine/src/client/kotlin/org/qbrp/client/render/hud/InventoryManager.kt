package org.qbrp.client.render.hud

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.ChatScreen
import net.minecraft.text.Text
import org.lwjgl.glfw.GLFW
import org.qbrp.client.render.inventory.InventoryWidget

class InventoryManager {
    private val openInventories = mutableListOf<InventoryWidget>()

    fun open(screen: InventoryWidget) {
        if (openInventories.find { it.containerId == screen.containerId} == null) {
            screen.init(MinecraftClient.getInstance())
            openInventories.add(screen)
            MinecraftClient.getInstance().setScreen(ChatScreen(""))
        }
    }

    fun close(id: String) {
        val screen = openInventories.find { it.containerId == id }
        if (screen != null) {
            openInventories.remove(screen)
            InventoryHudEvents.CLOSE.invoker().onClose(screen)
        }
    }

    fun closeAll() {
        openInventories.forEach { close(it) }
    }

    fun onHudRender(context: DrawContext, mouseX: Int, mouseY: Int, tickDelta: Float) {
        for (inv in openInventories) {
            inv.render(context, MinecraftClient.getInstance(), mouseX, mouseY)
        }
    }

    fun mouseDragged(mouseX: Double, mouseY: Double, button: Int) {
        if (MinecraftClient.getInstance().currentScreen is ChatScreen) {
            for (i in openInventories.indices.reversed()) {
                val screen = openInventories[i]
                screen.mouseDragged(mouseX, mouseY, button)
            }
        }
    }

    fun mouseReleased(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (MinecraftClient.getInstance().currentScreen is ChatScreen) {
            for (i in openInventories.indices.reversed()) {
                val screen = openInventories[i]
                if (screen.mouseReleased(mouseX, mouseY, button)) {
                    return true
                }
            }
        }
        return false
    }

    fun mouseClicked(mouseX: Double, mouseY: Double, button: Int) {
        val window = MinecraftClient.getInstance().window.handle
        if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_LEFT_SHIFT) == GLFW.GLFW_PRESS ||
            GLFW.glfwGetKey(window, GLFW.GLFW_KEY_RIGHT_SHIFT) == GLFW.GLFW_PRESS
        ) {
            for (inventory in openInventories) {
                if (inventory.isMouseHovered(mouseX.toInt(), mouseY.toInt())) {
                    close(inventory)
                    break
                }
            }
            return
        }

        if (MinecraftClient.getInstance().currentScreen is ChatScreen) {
            for (i in openInventories.indices.reversed()) {
                val screen = openInventories[i]
                screen.mouseClicked(mouseX, mouseY, button)
            }
        }
    }
}