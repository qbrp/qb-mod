package org.qbrp.client.render.ui.dsl

import net.minecraft.client.texture.TextureManager
import org.qbrp.client.core.EngineId
import org.qbrp.client.core.mouse.MouseState
import org.qbrp.client.render.dataformats.Vec2
import org.qbrp.client.render.dataformats.ZIndex
import org.qbrp.client.render.dataformats.vec2
import org.qbrp.client.render.ui.UiContext
import org.qbrp.client.render.ui.components.behaviour.ClickListener
import org.qbrp.client.render.ui.components.behaviour.CloseListener
import org.qbrp.client.render.ui.components.behaviour.Highlight
import org.qbrp.client.render.ui.components.behaviour.HighlightParams
import org.qbrp.client.render.ui.components.behaviour.HoverListener
import org.qbrp.client.render.ui.components.behaviour.OnClick
import org.qbrp.client.render.ui.components.behaviour.OnClose
import org.qbrp.client.render.ui.components.behaviour.OnHover
import org.qbrp.client.render.ui.components.layout.Anchors
import org.qbrp.client.render.ui.components.layout.Edges
import org.qbrp.client.render.ui.components.layout.FixedSize
import org.qbrp.client.render.ui.components.layout.Size
import org.qbrp.client.render.ui.components.layout.Sizing
import org.qbrp.client.render.ui.components.layout.SizingPolicy
import org.qbrp.client.render.ui.components.layout.linear.VerticalDirection
import org.qbrp.client.render.ui.components.text.TextSupplier
import org.qbrp.client.render.ui.components.texture.Texture
import org.qbrp.client.render.ui.components.tooltip.TooltipBackground
import org.qbrp.client.render.ui.components.transform.Pivot
import org.qbrp.client.render.ui.components.window.Views
import org.qbrp.client.render.ui.components.window.WindowManager
import org.qbrp.client.render.ui.view.HitBehavior
import org.qbrp.client.render.ui.view.View
import org.qbrp.client.render.ui.view.ViewComponent
import org.qbrp.client.render.ui.view.ViewContext
import org.qbrp.main.engine.text.components.TextComponent
import org.qbrp.main.engine.text.literal
import org.qbrp.main.mechanics.tooltip.TooltipColorProfile

typealias ViewComponentInit = (ViewContext) -> ViewComponent
typealias ViewBuildContinue = ViewScope.() -> Unit
typealias ViewPostCreate = View.() -> Unit

@UiDsl
open class ViewScope {
    internal val comps = mutableListOf<Pair<Int, Component>>()
    private val children = mutableListOf<Node>()
    var layout: Layout? = null
    private var fixedPos: Vec2? = null
    var minWidth = 0f
    var minHeight = 0f
    var maxWidth = Float.POSITIVE_INFINITY
    var maxHeight = Float.POSITIVE_INFINITY
    var customView: View? = null
    private var useAnchors: Boolean = true
    private var overlap: Boolean = true
    private var zIndex: ZIndex = ZIndex(0f)
    private var anchors = Anchors()
    private var margin: Edges = Edges()
    private var padding: Edges = Edges()
    protected var sizing = Sizing()
    private var topmost = false
    private var postCreate: MutableList<ViewPostCreate> = mutableListOf()
    private var visible = true
    private var hoverGap = 0f
    private var hoverHitTest: HitBehavior = HitBehavior.OPAQUE
    val futureReference: ViewRef = ViewRef()

    private fun assertPosNormalized(x: Float, y: Float) {
        require(x in 0f..1f && y in 0f..1f) { "Position should be normalized" }
    }

    fun invisible() {
        visible = false
    }

    fun overlayBehaviour() {
        topmost()
        denyHitTest()
    }

    fun topmost() {
        topmost = true
    }

    fun denyHitTest() {
        overlap = false
    }

    fun denyAnchors() {
        useAnchors = false
    }

    fun post(behaviour: ViewPostCreate) {
        postCreate += behaviour
    }

    fun viewManager(variants: Views) {
        component { WindowManager(it, variants) }
    }

    fun zIndex(index: Int) {
        zIndex = ZIndex(index.toFloat())
    }

    fun margin(l: Float = 0f, t: Float = 0f, r: Float = 0f, b: Float = 0f) {
        margin = Edges(l, t, r, b)
    }

    fun padding(l: Float, t: Float, r: Float, b: Float) {
        padding = Edges(l, t, r, b)
    }

    fun anchors(left: Float, top: Float, right: Float, bottom: Float, pivot: Pivot = Pivot()) {
        this.anchors = Anchors(left, top, right, bottom, pivot)
    }

    fun anchors(x: Float, y: Float, pivot: Pivot = Pivot()) {
        this.anchors = Anchors(x, y, x, y, pivot)
    }

    fun anchorCursor(mouseState: MouseState, pivot: Pivot = Pivot()) {
        this.anchors = Anchors.cursor(mouseState, pivot)
    }

    context(ui: UiContext)
    fun anchorCursor(pivot: Pivot = Pivot()) {
        anchorCursor(ui.mouse, pivot)
    }

    fun onClick(cont: Boolean = false, handle: ClickListener) {
        component { OnClick(it, handle, cont) }
    }

    fun onClick(handle: ClickListener, cont: Boolean = false) {
        component { OnClick(it, handle, cont) }
    }

    fun horizontalAlignment(pivot: Pivot = Pivot(0.5f, 0.0f)) {
        anchors(0.5f, 0.0f, pivot)
    }

    fun verticalAlignment(pivot: Pivot = Pivot(0.0f, 0.5f)) {
        anchors(0.0f, 0.5f, pivot)
    }

    fun fixedSize(width: Float, height: Float) {
        component { FixedSize(it, Size(width, height)) }
    }

    fun onClose(listener: CloseListener) {
        component { OnClose(it, listener) }
    }

    fun maxSize(width: Float = maxWidth, height: Float = maxHeight) {
        maxWidth = width
        maxHeight = height
    }

    fun fixedPos(x: Float, y: Float) {
        assertPosNormalized(x, y)
        fixedPos = vec2(x, y)
    }

    fun texture(id: EngineId, resolution: Float, u: Float = 0f, v: Float = 0f) {
        component { Texture(it, id, u, v, resolution, resolution, resolution, resolution) }
    }

    fun missingTexture() {
        component { Texture(it, TextureManager.MISSING_IDENTIFIER, 0f, 0f, 16f, 16f, 16f, 16f) }
    }

    fun background(color: Int, padding: Float = 0f) {
        component(BackgroundNode(color, padding), LOWEST)
    }

    fun text(textSupplier: TextSupplier) {
        component(TextNode(textSupplier), HIGH)
    }

    fun text(component: TextComponent) {
        text { component }
    }

    fun text(literal: String) {
        text(literal(literal))
    }

    fun vertical(gap: Float = 0f, direction: VerticalDirection = VerticalDirection.DOWN) { layout = VerticalNode(gap, direction) }

    fun horizontal(gap: Float = 0f) { layout = HorizontalNode(gap) }

    fun component(priority: Int = 5, build: (ViewContext) -> ViewComponent) {
        component(Custom(build), priority)
    }

    context(ui: UiContext)
    fun tooltipBackground(colorProfile: TooltipColorProfile) {
        component { ctx -> TooltipBackground(ctx, colorProfile, ui.tooltipBackgroundRenderer) }
    }

    fun component(component: Component, priority: Int = 5) {
        comps += priority to component
    }

    fun use(view: View) { customView = view }

    fun views(list: List<View>) {
        list.forEach { view(it) }
    }

    fun highlight(params: HighlightParams = HighlightParams.DEFAULT) {
        component(HIGHEST) { OnHover(it, Highlight(params)) }
    }

    fun view(block: ViewScope.() -> Unit) {
        children += ViewScope().apply(block).build()
    }

    fun view(v: View) {
        children += CustomView(v)
    }

    fun use(builder: ViewBuildContinue) {
        this.builder()
    }

    fun draggableBy(view: View) {
        component(DraggableBy(view))
    }

    context(ui: UiContext)
    fun draggableBy(viewNode: ViewNode) = draggableBy(viewNode.finish())

    fun sizing(width: SizingPolicy, height: SizingPolicy) {
        sizing = Sizing(width, height)
    }

    fun matchParent() {
        sizing = Sizing(SizingPolicy.MATCH_PARENT, SizingPolicy.WRAP)
    }

    fun wrap() {
        sizing = Sizing(SizingPolicy.WRAP, SizingPolicy.WRAP)
    }

    fun hover(b: HoverScope.() -> Unit) {
        HoverScope(this).apply { b() }
    }

    @UiDsl
    class HoverScope(private val viewScope: ViewScope) {
        fun hit(behavior: HitBehavior) {
            viewScope.hoverHitTest = behavior
        }

        fun translucent() {
            viewScope.hoverHitTest = HitBehavior.TRANSLUCENT
        }

        fun ignore() {
            viewScope.hoverHitTest = HitBehavior.IGNORE
        }

        fun gap(value: Float) {
            viewScope.hoverGap = value
        }

        fun listen(hoverListener: HoverListener) {
            viewScope.component { OnHover(it, hoverListener) }
        }
    }

    fun build() = ViewNode(
        layout,
        comps.sortedByDescending { it.first }.map { it.second }.toList(),
        children.toList(),
        minWidth,
        minHeight,
        maxWidth,
        maxHeight,
        customView,
        anchors,
        margin,
        padding,
        fixedPos,
        sizing,
        zIndex,
        useAnchors,
        overlap,
        topmost,
        { postCreate.forEach { it() } },
        futureReference,
        visible,
        hoverGap,
        hoverHitTest
    )

    companion object {
        const val HIGHEST = 10
        const val HIGH = 7
        const val MIDDLE = 5
        const val LOW = 3
        const val LOWEST = 0
    }
}

@UiDsl
context(ui: UiContext)
fun viewOf(builder: ViewScope.() -> Unit): View {
    return buildView(builder).finish()
}

@UiDsl
fun buildView(block: ViewScope.() -> Unit): ViewNode = ViewScope().apply(block).build()

@UiDsl
fun continueView(block: ViewBuildContinue): ViewBuildContinue = block