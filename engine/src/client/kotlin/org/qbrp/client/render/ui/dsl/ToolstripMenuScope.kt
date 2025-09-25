package org.qbrp.client.render.ui.dsl

import org.qbrp.client.core.EngineId
import org.qbrp.client.render.ui.UiContext
import org.qbrp.client.render.ui.components.behaviour.CloseListener
import org.qbrp.client.render.ui.components.dataview.ControlViewMark
import org.qbrp.client.render.ui.components.dataview.PopupMenu
import org.qbrp.client.render.ui.components.dataview.PopupMenuItem
import org.qbrp.client.render.ui.components.dataview.PopupMenuItemClickListener
import org.qbrp.client.render.ui.components.dataview.ToolstripOrientation
import org.qbrp.client.render.ui.components.dataview.itemControlView
import org.qbrp.main.engine.text.components.TextComponent
import org.qbrp.main.engine.text.emptyText
import org.qbrp.main.engine.text.literal
import org.qbrp.main.mechanics.tooltip.TooltipViewData
import javax.tools.Tool

@UiDsl
context(ui: UiContext)
fun ViewScope.toolstrip(overridenPopupMenu: PopupMenu? = null, builder: ItemControlScope.() -> Unit) {
    return ItemControlScope(overridenPopupMenu = overridenPopupMenu).apply { builder() }.buildView(this)
}

@UiDsl
interface ItemControlBuilder {
    fun item(builder: ItemScope.() -> Unit)
}

@UiDsl
class ItemControlScope(
    internal var viewBuild: ViewBuildContinue = {},
    internal var closeOnClick: Boolean = true,
    private var parentViewId: String? = null,
    private var controlGap: Float = 0f,
    private val orientation: ToolstripOrientation = ToolstripOrientation.VERTICAL,
    private val overridenPopupMenu: PopupMenu? = null
) : ItemControlBuilder {
    internal val items: MutableList<PopupMenuItem> = mutableListOf()
    internal var onClose: CloseListener = CloseListener {}

    fun parent(id: String) {
        parentViewId = id
    }

    fun gap(value: Float) {
        controlGap = value
    }

    override fun item(builder: ItemScope.() -> Unit) {
        items += ItemScope(
            ItemControlScope(
                viewBuild,
                closeOnClick,
                null,
                controlGap,
                orientation,
                overridenPopupMenu
            )
        ).apply { builder() }.build()
    }

    fun root(build: ViewBuildContinue) {
        viewBuild = build
    }

    fun onClose(listener: CloseListener) {
        this.onClose = listener
    }

    fun eternal() {
        closeOnClick = false
    }

    fun buildPopupMenu(): PopupMenu {
        return PopupMenu(items, closeOnClick, onClose, parentViewId, controlGap)
    }

    context(ui: UiContext)
    internal fun buildView(view: ViewScope) {
        view.component { ControlViewMark(it) }
        view.itemControlView(overridenPopupMenu ?: buildPopupMenu(), null, orientation, viewBuild)
    }
}

@UiDsl
class ItemScope(private val itemControlScope: ItemControlScope): ItemControlBuilder by itemControlScope {
    private var label: TextComponent = emptyText()
    private var icon: IconPrefab? = null
    private var clickListener: PopupMenuItemClickListener? = null
    private var style: PopupMenuItem.Style = PopupMenuItem.Style(null)
    private var tooltip: TooltipViewData? = null

    fun tooltip(data: TooltipViewData) {
        tooltip = data
    }

    fun style(style: PopupMenuItem.Style) {
        this.style = style
    }

    fun onClickItem(listener: PopupMenuItemClickListener) {
        clickListener = listener
    }

    fun label(text: TextComponent) {
        label = text
    }

    fun label(literal: String) {
        label = literal(literal)
    }

    fun icon(ic: IconPrefab) {
        icon = ic
    }

    fun icon(assetId: EngineId) {
        icon = IconPrefab.Asset(assetId)
    }

    internal fun build(): PopupMenuItem {
        return PopupMenuItem(style, label, icon, clickListener, itemControlScope.buildPopupMenu(), tooltip)
    }
}