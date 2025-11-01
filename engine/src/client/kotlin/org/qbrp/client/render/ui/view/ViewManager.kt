package org.qbrp.client.render.ui.view

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.ChatScreen
import org.qbrp.client.core.mouse.MouseState
import org.qbrp.client.core.mouse.events.MouseClickEvent
import org.qbrp.client.core.notifications.ClientNotificationSender
import org.qbrp.client.render.dataformats.Vec2
import org.qbrp.client.render.screen.context.Draw2dContext
import org.qbrp.client.render.window.Window
import org.qbrp.main.core.events.on
import org.qbrp.main.network.player.Notification
import java.util.concurrent.CopyOnWriteArrayList

class ViewManager(
    private val screen: Screen,
    private val notificationManager: ClientNotificationSender,
    private val mouse: MouseState,
) {
    val openViews: CopyOnWriteArrayList<View> get() = screen.children
    private val openViewsWithIds = mutableMapOf<String, View>()

    private val currentScreen get() = MinecraftClient.getInstance().currentScreen
    private val blockRender get() = currentScreen != null && currentScreen !is ChatScreen
    private val blockInteractions get() = blockRender && mouse.isLocked

    fun getViewGroup(
        views: List<View> = listOf(),
        closeAllOnClickOutView: Boolean = false,
        removeCallback: () -> Unit = {}
    ): ViewGroup {
        return ViewGroup(closeAllOnClickOutView).apply {
            val listener = on<MouseClickEvent> { onClick(pos.x, pos.y) }
            this.removeCallback = { removeCallback() }
            this.destroyCallback = { listener.unsubscribe() }
            views.forEach { add(it) }
        }
    }

    fun onRender(ctx: Draw2dContext, mousePos: Vec2) {
        try {
            if (!blockInteractions) screen.onMouse(mousePos.x, mousePos.y)
            if (!blockRender) screen.render(ctx)
        } catch (e: Exception) {
            handleError(e, "Отрисовка")
        }
    }

    fun getView(id: String): View {
        return getViewOrNull(id)!!
    }

    fun getViewOrNull(id: String): View? {
        return openViewsWithIds[id]
    }

    fun addView(view: View) {
        try {
            view.parent = screen
            screen.addView(view)
        } catch (e: Exception) {
            handleError(e, "Инициализация UI-элемента")
        }
    }

    fun tick() {
        openViews.forEach { tickView(it) }
    }

    fun frame() {
        openViews.forEach { tickFrame(it) }
    }

    private fun tickView(v: View) {
        v.viewComponents.forEach { it.onTick() }
        v.children.forEach { tickView(it) }
    }

    private fun tickFrame(v: View) {
        v.viewComponents.forEach { it.onFrame() }
        v.children.forEach { tickFrame(it) }
    }

    fun isOpen(view: View) = openViews.contains(view)

    fun isOpen(slotId: String) = openViewsWithIds.containsKey(slotId)

    fun openView(view: View, slotId: String, focus: Boolean = true, override: Boolean = false) {
        if (openViewsWithIds.containsKey(slotId)) {
            if (override) {
                closeView(slotId)
            } else {
                return
            }
        }
        addView(view)
        if (focus) mouse.unlock()
        openViewsWithIds[slotId] = view
    }

    fun closeView(slotId: String) {
        openViewsWithIds[slotId]?.let { closeView(it) }
    }

    fun closeView(view: View) {
        try {
            screen.removeView(view)
            openViewsWithIds.entries.find { it.value == view }?.also { openViewsWithIds.remove(it.key) }
        } catch (e: Exception) {
            handleError(e, "Закрытие UI-элемента")
        }
    }

    fun mouseClicked(pos: Vec2, button: Int): Boolean {
        if (blockInteractions) return false
        return try {
            screen.onMouseClick(pos.x, pos.y, button)
        } catch (e: Exception) {
            handleError(e, "Обработка Mouse-Click")
            false
        }
    }

    fun mouseDragged(pos: Vec2) {
        if (blockInteractions) return
        try {
            screen.onMouseDragged(pos.x, pos.y)
        } catch (e: Exception) {
            handleError(e, "Обработка Mouse-Drag")
        }
    }

    fun mouseReleased(pos: Vec2, button: Int): Boolean {
        if (blockInteractions) return false
        return try {
            screen.onMouseReleased(pos.x, pos.y, button)
        } catch (e: Exception) {
            handleError(e, "Обработка Mouse-Release")
            return false
        }
    }

    fun onResize(client: MinecraftClient, window: Window) {
        try {
            screen.onResize(client, window)
        } catch (e: Exception) {
            handleError(e, "Смена размера окна")
        }
    }

    fun handleError(e: Exception, ctx: String) {
        val notification = Notification.error("Ошибка рендеринга UI", ctx, e)
        notificationManager.send(notification)
        e.printStackTrace()
    }

    fun closeAll() {
        openViews.toList().forEach { screen.removeView(it) }
        openViewsWithIds.clear()
    }
}