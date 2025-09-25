package org.qbrp.client.render.ui.view

import net.minecraft.client.MinecraftClient
import org.joml.Quaternionf
import org.qbrp.client.render.Drawable
import org.qbrp.client.render.dataformats.MutableVec2
import org.qbrp.client.render.dataformats.Vec2
import org.qbrp.client.render.dataformats.ZIndex
import org.qbrp.client.render.dataformats.vec2
import org.qbrp.client.render.screen.context.Draw2dContext
import org.qbrp.client.render.ui.components.event.Clickable
import org.qbrp.client.render.ui.components.event.Hoverable
import org.qbrp.client.render.ui.components.event.MouseClick
import org.qbrp.client.render.ui.components.event.MouseDrag
import org.qbrp.client.render.ui.components.event.MouseRelease
import org.qbrp.client.render.window.Window
import org.qbrp.main.core.components.AnyComponent
import org.qbrp.main.core.components.AssociatedComponentState
import org.qbrp.main.core.components.ComponentManager
import org.qbrp.main.core.components.construct.CtxComponentBuilderOperator
import org.qbrp.main.core.components.state.ComponentState
import org.qbrp.main.core.events.allFalse
import java.util.concurrent.CopyOnWriteArrayList
import java.util.function.Consumer

typealias ViewState = AssociatedComponentState<View>
typealias ViewStateImpl = ComponentState<View>

open class View(
    open var width: Float = 50f,
    open var height: Float = 50f,
    protected open var _pos: MutableVec2 = MutableVec2.of(DEFAULT_POS),
    var zIndex: ZIndex = ZIndex(0f),
    val componentManager: ViewState = ViewStateImpl(),
) : ComponentManager by componentManager, CtxComponentBuilderOperator<ViewContext> {
    override val componentContext: ViewContext get() = ViewContext(componentManager)

    init {
        componentManager.parent = this
    }

    lateinit var parent: View
    var children: CopyOnWriteArrayList<View> = CopyOnWriteArrayList()
        protected set
    val viewComponents get() = getComponents() as List<ViewComponent>
    var topmost = false
    var visible = true
    var input = true
    var hoverGap = 0f
    var hitBehavior: HitBehavior = HitBehavior.OPAQUE
    var isSelfHover = false

    open val x: Float get() = _pos.x
    open val y: Float get() = _pos.y

    val pos: Vec2 get() = vec2(_pos)

    fun setPos(x: Float = _pos.x, y: Float = _pos.y) {
        _pos = MutableVec2(x, y)
    }

    val globalPos: Vec2
        get() = if (!isScreen) parent.globalPos.add(pos) else vec2(pos)
    val screen: Screen
        get() = if (!isScreen) parent.screen else (this as Screen)
    val isScreen
        get() = !::parent.isInitialized && this is Screen

    var rotate: Float = 0f

    fun localTo(target: View, p: Vec2): Vec2 =
        vec2(this.globalPos.x + p.x - target.globalPos.x,
            this.globalPos.y + p.y - target.globalPos.y)

    fun render(
        ctx: Draw2dContext,
    ) {
        render(ctx, TransformationContext(0f, 0f))
    }

    open fun render(
        ctx: Draw2dContext,
        parentTctx: TransformationContext,
    ) {
        ctx.matrices.push()

        children.forEach { it.onTransform(ctx) }

        onTransform(ctx)
        ctx.matrices.translate(_pos.x.toDouble(), _pos.y.toDouble(), zIndex.value.toDouble())
        if (rotate != 0f) {
            val radians = Math.toRadians(rotate.toDouble()).toFloat()
            ctx.matrices.translate(width / 2.0, height / 2.0, 0.0)
            val quat = Quaternionf().rotationZ(radians)
            ctx.matrices.multiply(quat)
            ctx.matrices.translate(-width / 2.0, -height / 2.0, 0.0)
        }

        if (visible) {
            val drawables = viewComponents.filterIsInstance<Drawable>()
            val myTctx = parentTctx.withOffset(_pos.x, _pos.y)
            initRender(ctx, drawables)
            onRender(ctx, drawables)
            for (child in children.toList()) {
                try {
                    child.render(ctx, myTctx)
                } catch (e: Exception) {
                    removeView(child)
                    throw e
                }
            }
            endRender(ctx, drawables)
        }

        ctx.matrices.pop()
    }

    fun onMouse(mx: Float, my: Float) {
        if (!input) return
        val hoverables = filterIsInstance<Hoverable>()
        handleMouse(mx, my)
        updateMouseHover(mx, my, hoverables)
    }

    fun handleMouse(mx: Float, my: Float) {
        if (!input) return
        viewComponents.forEach { it.onMouse(mx, my) }
        children.forEach {
            val lx = mx - pos.x
            val ly = my - pos.y
            it.handleMouse(lx, ly)
        }
    }

    fun updateMouseHover(
        mx: Float,
        my: Float,
        hoverables: List<Hoverable> = filterIsInstance<Hoverable>(),
        exit: Boolean = false
    ): HitResult {
        if (!input || hitBehavior == HitBehavior.IGNORE || exit) {
            if (isSelfHover) {
                isSelfHover = false
                hoverables.forEach { it.onHoverExit() }
            }
            return HitResult.NONE
        }

        var childClaimed = false

        for (child in children.reversed()) {
            val pos = child.pos
            val lx = mx - pos.x
            val ly = my - pos.y
            if (!childClaimed) {
                when (child.updateMouseHover(lx, ly)) {
                    HitResult.SELF, HitResult.DESCENDANT -> {
                        childClaimed = true
                    }
                    HitResult.NONE -> {}
                }
            } else {
                child.updateMouseHover(lx, ly, exit = true)
            }
        }

        val hitSelf = mx in -hoverGap..(width + hoverGap) && my in -hoverGap..(height + hoverGap)

        return when (hitBehavior) {
            HitBehavior.OPAQUE -> {
                if (!childClaimed && hitSelf) {
                    if (!isSelfHover) hoverables.forEach { it.onHoverEnter() }
                    hoverables.forEach { it.onHover(mx, my) }
                    isSelfHover = true
                    return HitResult.SELF
                } else {
                    if (isSelfHover) {
                        isSelfHover = false
                        hoverables.forEach { it.onHoverExit() }
                    }
                    return if (childClaimed) HitResult.DESCENDANT else HitResult.NONE
                }
            }

            HitBehavior.TRANSLUCENT -> {
                if (hitSelf) {
                    if (!isSelfHover) hoverables.forEach { it.onHoverEnter() }
                    hoverables.forEach { it.onHover(mx, my) }
                    isSelfHover = true
                } else if (isSelfHover) {
                    isSelfHover = false
                    hoverables.forEach { it.onHoverExit() }
                }
                return when {
                    childClaimed -> HitResult.DESCENDANT
                    hitSelf -> HitResult.SELF
                    else -> HitResult.NONE
                }
            }

            HitBehavior.IGNORE -> HitResult.NONE // сюда не попадём из-за раннего return
        }
    }

    fun onResize(client: MinecraftClient, window: Window) {
        init()
        viewComponents.forEach { it.onResize(client, window) }
        children.forEach { it.onResize(client, window) }
    }

    open fun init() {
        children.forEach { it.init() }
        viewComponents.forEach { it.onInit() }
    }

    open fun addView(view: View) {
        view.parent = this
        children.add(view)
        sortViews()
        view.init()
        viewComponents.forEach { it.onAddView(view) }
    }

    open fun addViews(list: List<View>) {
        list.forEach { addView(it) }
    }

    fun removeSelf() {
        parent.removeView(this)
    }

    open fun removeView(view: View) {
        children.remove(view)
        view.viewComponents.forEach { it.onRemoved() }
        sortViews()
    }

    fun removeAllViews() {
        children.toList().forEach { removeView(it) }
    }

    private fun sortViews() {
        val list = children.toList()
            .sortedBy {
                when(it.topmost) {
                    false -> 0
                    true -> 1
                }
            }
            .sortedBy { it.zIndex.value }
        children = CopyOnWriteArrayList(list)
    }

    fun setViewAtIndex(view: View, index: Int) {
        children[index] = view
        view.parent = this
        view.init()
    }

    fun bringToFront() {
        val frontChild = parent.children.filterNot { it.topmost }.maxBy { it.zIndex.value }
        val frontChildZIndex = frontChild.zIndex
        if (zIndex.value >= frontChildZIndex.value) return
        frontChild.zIndex = zIndex
        zIndex = frontChildZIndex
    }

    open fun onTransform(ctx: Draw2dContext) {
        viewComponents
            .forEach { it.onTransform(ctx) }
    }

    open fun onRender(uiCtx: Draw2dContext, drawables: List<Drawable>) {
        drawables.forEach {
            it.onRender(uiCtx)
        }
    }

    open fun endRender(uiCtx: Draw2dContext, drawables: List<Drawable>) {
        drawables.forEach { it.endRender(uiCtx) }
    }

    open fun initRender(uiCtx: Draw2dContext, drawables: List<Drawable>) {
        drawables.forEach { it.initRender(uiCtx) }
    }

    fun onClick(button: Int, lx: Float, ly: Float): Boolean {
        return viewComponents
            .filterIsInstance<Clickable>()
            .map { it.onClick(button, lx, ly) }
            .allFalse()
    }

    fun inViewGlobal(x: Float, y: Float): Boolean {
        return x >= this.x && x < this.x + width && y >= this.y && y < this.y + height
    }

    fun inViewLocal(x: Float, y: Float): Boolean {
        return x >= 0f && x < width && y >= 0f && y < height
    }

    fun onMouseClick(
        mX: Float,
        mY: Float,
        button: Int
    ): Boolean {
        if (!input) return false
        viewComponents
            .filterIsInstance<MouseClick>()
            .forEach { if (it.onClick(mX, mY, button)) return true }

        if (mX >= 0f && mX < width && mY >= 0f && mY < height) {
            if (!onClick(button, mX, mY)) return true
        }

        children.toList().forEach {
            val lX = mX - it._pos.x
            val lY = mY - it._pos.y
            if (it.onMouseClick(lX, lY, button)) return true
        }
        return false
    }

    fun onMouseReleased(
        x: Float,
        y: Float,
        button: Int
    ): Boolean {
        if (!input) return false
        viewComponents.filterIsInstance<MouseRelease>()
            .forEach { if (it.onMouseRelease(x, y, button)) return true }

        children.forEach {
            val lX = x - it._pos.x
            val lY = y - it._pos.y
            if (it.onMouseReleased(lX, lY, button)) return true
        }

        return false
    }

    fun onMouseDragged(
        mX: Float,
        mY: Float,
    ) {
        if (!input) return
        viewComponents.filterIsInstance<MouseDrag>()
            .forEach { it.onMouseDragged(mX, mY) }

        children.forEach {
            val lX = mX - it._pos.x
            val lY = mY - it._pos.y
            it.onMouseDragged(lX, lY)
        }
    }

    override fun forEach(action: Consumer<in AnyComponent>?) {
        componentManager.forEach(action)
    }

    companion object {
        val DEFAULT_POS = vec2(10, 10)
    }
}
