package org.qbrp.client.render.ui.dsl

import org.qbrp.client.render.animation.Animation
import org.qbrp.client.render.dataformats.vec2
import org.qbrp.client.render.ui.components.behaviour.Draggable
import org.qbrp.client.render.ui.components.behaviour.Translation
import org.qbrp.client.render.ui.components.effect.Cutout
import org.qbrp.client.render.ui.components.layout.Edges
import org.qbrp.client.render.ui.view.View
import org.qbrp.main.core.components.extensions.add
import org.qbrp.main.core.components.extensions.get
import org.qbrp.main.core.components.extensions.getAs
import org.qbrp.main.core.components.extensions.getOrAdd
import org.qbrp.main.core.components.extensions.remove

private const val SLIDE_KEY = "Translation#animationSlide"
private const val CUTOUT_KEY = "Cutout#hoverSlideUp"

@UiDsl
fun AssociatedAnimationScope.hoverSlideUp(
    blockInput: List<View> = listOf(),
    draggableRef: (() -> View)? = null,
    block: SlideAnimationScope.() -> Unit = {}
) {
    val cfg = SlideAnimationScope().apply(block)
    var mode = SlideMode.APPEAR
    val animation by lazy { animationReference.get() }
    var hover = true
    var dragInitialized = false

    viewScope.invisible()
    values(0f, 1f)

    fun appear(view: View, anim: Animation) {
        cfg.appear(view, anim)
        anim.cancel()
        mode = SlideMode.APPEAR
        anim.start()
    }

    fun disappear(view: View, anim: Animation) {
        cfg.disappear(view, anim)
        anim.cancel()
        mode = SlideMode.DISAPPEAR
        anim.start()
    }

    fun initDrag() {
        draggableRef?.invoke()?.apply {
            get<Draggable>()?.let {
                it.onStartDrag = {
                    hover = false
                    disappear(this, animation)
                }
                it.onReleaseDrag = {
                    hover = true
                }
            }
        }
    }

    onStart { view, animation ->
        if (!dragInitialized) {
            initDrag()
            dragInitialized = true
        }
        blockInput.forEach { it.input = false }
        view.getOrAdd(setName = CUTOUT_KEY) {
            Cutout(view.componentContext) { v ->
                (cfg.scissor?.invoke(v)) ?: Edges(-v.width, 0f, v.width * 2f, v.height)
            }
        }
        view.getOrAdd(setName = SLIDE_KEY) { Translation(view.componentContext, vec2(0f, view.height * animation.value)) }
        view.visible = true
    }

    onUpdate { view, t ->
        val translation = vec2(0f, view.height * t)
        view.getAs<Translation>(SLIDE_KEY)?.vec = translation
    }

    onEnd { view, _ ->
        view.removeComponent(SLIDE_KEY)
        view.removeComponent(CUTOUT_KEY)
        if (mode == SlideMode.DISAPPEAR) {
            view.visible = false
            mode = SlideMode.APPEAR
        } else {
            view.visible = true
        }
        blockInput.forEach { it.input = true }
    }

    onCancel { _, view ->
        view.removeComponent(SLIDE_KEY)
        view.removeComponent(CUTOUT_KEY)
    }

    hover {
        start { view, anim ->
            if (hover) {
                appear(view, anim)
            }
        }
        end { view, anim ->
            if (hover) disappear(view, anim)
        }
    }
}

enum class SlideMode { APPEAR, DISAPPEAR }

@UiDsl
class SlideAnimationScope {
    internal var appear: (view: View, animation: Animation) -> Unit = { _, a ->
        a.from = 1f
        a.to = 0f
    }

    internal var disappear: (view: View, animation: Animation) -> Unit = { _, a ->
        a.from = 0f
        a.to = 1f
    }

    var scissor: ((view: View) -> Edges)? = { v ->
        Edges(-v.width, 0f, v.width * 2f, v.height)
    }

    fun appear(block: (view: View, animation: Animation) -> Unit) {
        appear = block
    }

    fun disappear(block: (view: View, animation: Animation) -> Unit) {
        disappear = block
    }
}