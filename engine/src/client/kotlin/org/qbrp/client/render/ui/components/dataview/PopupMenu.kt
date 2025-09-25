package org.qbrp.client.render.ui.components.dataview

import net.minecraft.client.MinecraftClient
import org.lwjgl.glfw.GLFW
import org.qbrp.client.render.Render
import org.qbrp.client.render.dataformats.Vec2
import org.qbrp.client.render.dataformats.vec2
import org.qbrp.client.render.ui.UiContext
import org.qbrp.client.render.ui.components.behaviour.CloseListener
import org.qbrp.client.render.ui.components.behaviour.ShowTooltip
import org.qbrp.client.render.ui.components.event.Clickable
import org.qbrp.client.render.ui.components.event.Hoverable
import org.qbrp.client.render.ui.components.event.MouseClick
import org.qbrp.client.render.ui.components.layout.normalizedPosOf
import org.qbrp.client.render.ui.components.transform.RectTransform
import org.qbrp.client.render.ui.dsl.IconPrefab
import org.qbrp.client.render.ui.dsl.UiDsl
import org.qbrp.client.render.ui.dsl.ViewBuildContinue
import org.qbrp.client.render.ui.dsl.ViewScope
import org.qbrp.client.render.ui.dsl.icon
import org.qbrp.client.render.ui.dsl.viewOf
import org.qbrp.client.render.ui.view.View
import org.qbrp.client.render.ui.view.ViewComponent
import org.qbrp.client.render.ui.view.ViewContext
import org.qbrp.client.render.ui.view.isHovered
import org.qbrp.client.render.window.Window
import org.qbrp.main.core.components.extensions.add
import org.qbrp.main.core.components.extensions.get
import org.qbrp.main.core.components.extensions.require
import org.qbrp.main.engine.text.components.TextComponent
import org.qbrp.main.mechanics.tooltip.TooltipViewData

// Menu

interface ItemControl {
    val items: List<PopupMenuItem>
    val closeOnItemClick: Boolean
    val closeListener: CloseListener
    val parentViewId: String?
    val controlGap: Float
}

interface PopupMenuItemClickListener {
    fun onMenuItemClicked(button: Int, pos: Vec2, item: PopupMenuItem): Boolean
}

data class PopupMenu(
    override val items: List<PopupMenuItem>,
    override val closeOnItemClick: Boolean,
    override val closeListener: CloseListener,
    override val parentViewId: String?,
    override val controlGap: Float
) : ItemControl

data class PopupMenuItem(
    val style: Style,
    val label: TextComponent,
    val icon: IconPrefab?,
    val clickListener: PopupMenuItemClickListener?,
    val control: ItemControl,
    val tooltip: TooltipViewData?
) : ItemControl by control {
    data class Style(val background: Int?)
}

context(ui: UiContext)
fun popupMenuItemView(item: PopupMenuItem, close: Boolean) = viewOf {
    matchParent()
    horizontal(4f)
    highlight()
    hover {
        translucent()
        item.tooltip?.let {
            listen(ShowTooltip(it, ui.tooltipManager, ui))
        }
    }
    item.style.background?.let {
        background(it)
    }
    view {
        fixedSize(8f, 8f)
        item.icon?.let { icon(it, 8f) }
    }
    view {
        matchParent()
        text(item.label)
    }
    if (item.items.isNotEmpty()) {
        view {
            wrap()
            texture(Render.DOT, 16f)
        }
    }
}

enum class ToolstripOrientation {
    VERTICAL, HORIZONTAL
}

@UiDsl
context(ui: UiContext)
fun ViewScope.itemControlView(
    control: ItemControl,
    parent: ItemControlComponent? = null,
    orientation: ToolstripOrientation = ToolstripOrientation.VERTICAL,
    controlViewBuilder: ViewBuildContinue = {}
) {
    onClose(control.closeListener)
    hover { translucent() }
    when (orientation) {
        ToolstripOrientation.VERTICAL -> vertical(1f)
        ToolstripOrientation.HORIZONTAL -> horizontal(1f)
    }

    val positionResolver =
        control.parentViewId?.let { ViewManagerPositionResolver(it) } ?: ChildControlPositionResolverImpl()

    component { ctx ->
        ItemControlComponent(ctx, control, parent, controlViewBuilder, positionResolver, ui).apply {
            invalidate()
        }
    }
}

context(ui: UiContext)
internal fun getItemViews(control: ItemControl): List<ItemViewInfo> =
    control.items.map { ItemViewInfo(popupMenuItemView(it, control.closeOnItemClick), it) }

internal data class ItemViewInfo(val view: View, val item: PopupMenuItem)

fun View.anyChildrenIn(x: Float, y: Float): Boolean {
    return children.any { it.inViewGlobal(x, y) }
}

fun View.childrenIn(x: Float, y: Float): List<View> {
    return children.filter { it.inViewGlobal(x, y) }
}

context(ui: UiContext)
fun View.rightEdgeGlobalPosition(gap: Float): Vec2 {
    val right = globalPos.x + width + gap
    val mouseY = globalPos.y
    return vec2(right, mouseY)

}
interface ChildControlPositionResolver {
    context(ui: UiContext) fun resolveGlobalPos(control: ItemControl, menuView: View, itemView: View): Vec2
}

class ChildControlPositionResolverImpl() : ChildControlPositionResolver {
    context(ui: UiContext)
    override fun resolveGlobalPos(control: ItemControl, menuView: View, itemView: View): Vec2 {
        return itemView.rightEdgeGlobalPosition(control.controlGap)
    }
}

class ViewManagerPositionResolver(val id: String) : ChildControlPositionResolver {
    context(ui: UiContext)
    override fun resolveGlobalPos(control: ItemControl, menuView: View, itemView: View): Vec2 {
        val container = ui.viewManager.getView(id)
        val right = container.globalPos.x + container.width + control.controlGap
        val mouseY = itemView.globalPos.y
        return vec2(right, mouseY)
    }
}

class ControlViewMark(ctx: ViewContext): ViewComponent(ctx)

class ItemControlComponent(
    ctx: ViewContext,
    val control: ItemControl,
    val parent: ItemControlComponent? = null,
    private val itemControlBuild: ViewBuildContinue,
    private val positionResolver: ChildControlPositionResolver,
    private val ui: UiContext
) : ViewComponent(ctx), MouseClick {
    var hoverGap: Float = 8f
    var childControl: View? = null
        private set

    context(ui: UiContext)
    fun hoveredSelfOrParent(): Boolean {
        val selfHover = view.isHovered(hoverGap)
        val parentHovered = control.parentViewId?.let { ui.viewManager.getViewOrNull(it)?.isHovered(hoverGap) == true } == true
        return selfHover || parentHovered
    }

    fun isHovered(): Boolean {
        return with(ui) {
            val childrenHovered = childControl?.let { it.isHovered(hoverGap) || it.get<ItemControlComponent>()?.isHovered() == true } == true
            childrenHovered || hoveredSelfOrParent()
        }
    }

    fun isHoveredExtended(): Boolean {
        return with(ui) {
            val childrenHovered = childControl?.let {
                val rightSide = ui.mouse.pos.x > it.globalPos.x
                it.isHovered(hoverGap) || rightSide || it.get<ItemControlComponent>()?.isHoveredExtended() == true
            } == true
            childrenHovered || hoveredSelfOrParent()
        }
    }

    init {
        invalidate()
    }

    fun invalidate() = with(ui) {
        view.removeAllViews()
        getItemViews(control).forEachIndexed { index, i ->
            val item = i.item
            val v = i.view
            v.add(PopupItemComponent(v.componentContext, index, item, this@ItemControlComponent, ui))
            view.addView(v)
        }
        view.require<RectTransform>().invalidateMeasure()
    }

    // возвращаем созданный попап
    fun openChildControl(id: Int): View = with(ui) {
        val item = control.items[id]
        val screenPos = view.screen.normalizedPosOf(
            positionResolver.resolveGlobalPos(control, view, view.children.first { it.isSelfHover })
        )

        val popup = viewOf {
            itemControlView(item, parent = this@ItemControlComponent, controlViewBuilder = itemControlBuild)
            anchors(screenPos.x, screenPos.y)
            overlayBehaviour()
            use(itemControlBuild)
        }
        childControl = popup
        popup.require<ItemControlComponent>().invalidate()

        viewManager.addView(popup)
        popup
    }

    fun closeChildControl() {
        val popup = childControl
        if (popup == null) return
        popup.require<ItemControlComponent>().close()
        childControl = null
    }

    fun close() {
        view.removeSelf()
        closeChildControl()
    }

    fun teardown(): Unit = with(ui) {
        close()
        parent?.teardown()
    }

    override fun onMouse(mX: Float, mY: Float) {
        if (childControl != null && !isHoveredExtended()) {
            closeChildControl()
        }
    }

    override fun onResize(client: MinecraftClient, window: Window) {
        close()
    }

    override fun onClick(lmX: Float, lmY: Float, button: Int): Boolean = with(ui) {
        return if (!isHovered()) {
            close()
            true
        } else {
            false
        }
    }
}


class PopupItemComponent(
    ctx: ViewContext,
    val id: Int,
    val data: PopupMenuItem,
    private val control: ItemControlComponent,
    private val ui: UiContext
) : ViewComponent(ctx), Hoverable, Clickable {
    var hovered = false
        private set

    private var hoveredTime = 0

    override fun onHover(x: Float, y: Float): Unit = with(ui) {
        // если курсор ушёл с анкёра и НИ один из открытых попапов/их потомков не ховерится — закрываем свой попап
        if (control.childControl == null && hoveredTime > TIME_TO_SHOW_ITEMS && !data.items.isEmpty()) {
            control.openChildControl(id)
        }
        true
    }

    override fun onHoverEnter() {
        hoveredTime = 0
        hovered = true
    }

    override fun onHoverExit() {
        hovered = false
    }

    override fun onTick() {
        if (hovered) {
            hoveredTime += 1
            return
        }
    }

    override fun onClick(button: Int, lx: Float, ly: Float): Boolean {
        val clickListener = data.clickListener
        val children = data.items
        return if (button == GLFW.GLFW_MOUSE_BUTTON_1) {
            if (clickListener != null) {
                val handled = clickListener.onMenuItemClicked(button, vec2(lx, ly), data)
                if (handled) {
                    control.teardown()
                    true
                } else {
                    false
                }
            } else if (children.isNotEmpty() && control.childControl == null) {
                control.openChildControl(id)
                true
            } else {
                false
            }
        } else {
            false
        }
    }

    companion object {
        const val TIME_TO_SHOW_ITEMS = 5
    }
}