package org.qbrp.client.render.ui.dsl

import org.qbrp.client.render.animation.Animation
import org.qbrp.client.render.animation.Easing
import org.qbrp.client.render.screen.context.Draw2dContext
import org.qbrp.client.render.ui.components.animation.AnimationManager
import org.qbrp.client.render.ui.components.behaviour.HoverListener
import org.qbrp.client.render.ui.components.behaviour.OnHover
import org.qbrp.client.render.ui.view.View
import org.qbrp.client.render.ui.view.ViewComponent
import org.qbrp.main.core.components.extensions.require

interface AnimationBehaviourBuilder {
    fun onStart(behaviour: () -> Unit)
    fun onUpdate(behaviour: (Float) -> Unit)
    fun onEnd(behaviour: () -> Unit)
    fun onCancel(behaviour: () -> Unit)
}

interface AnimationBuilder {
    val animationReference: AnimationRef
    fun values(from: Float, to: Float)
    fun duration(millis: Float)
    fun easing(lazy: Easing.() -> (Float) -> Float)
    fun repeats(count: Int)
    fun yoyo()
    fun once()
    fun loop()
    fun startDelay(value: Float)
}

@UiDsl
open class AnimationScope : AnimationBuilder, AnimationBehaviourBuilder {
    private var from: Float = 0f
    private var to: Float = 1f
    private var durationMillis: Float = 300f
    private var easing: (Float) -> Float = Easing.LINEAR
    private var repeat: Int = -1
    private var yoyo: Boolean = false
    private var startDelayMillis: Float = 0f
    private var onStart: (() -> Unit)? = null
    private var onUpdate: (Float) -> Unit = { _ -> }
    private var onEnd: (() -> Unit)? = null
    private var onCancel: (() -> Unit)? = null
    override val animationReference: AnimationRef = AnimationRef()

    override fun values(from: Float, to: Float) {
        this.from = from
        this.to = to
    }

    override fun duration(millis: Float) {
        durationMillis = millis
    }

    override fun easing(lazy: Easing.() -> (Float) -> Float) {
        easing = Easing.lazy()
    }

    override fun repeats(count: Int) {
        this.repeat = count
    }

    override fun yoyo() {
        yoyo = true
    }

    override fun once() {
        repeat = 0
    }

    override fun loop() {
        repeat = -1
    }

    override fun startDelay(value: Float) {
        startDelayMillis = value
    }

    override fun onStart(behaviour: () -> Unit) {
        onStart = behaviour
    }

    override fun onUpdate(behaviour: (Float) -> Unit) {
        onUpdate = behaviour
    }

    override fun onEnd(behaviour: () -> Unit) {
        onEnd = behaviour
    }

    override fun onCancel(behaviour: () -> Unit) {
        onCancel = behaviour
    }

    fun build() = Animation.of(
        from,
        to,
        durationMillis,
        easing,
        repeat,
        yoyo,
        startDelayMillis,
        onUpdate,
        onStart,
        onEnd,
        onCancel
    ).also {
        animationReference.set(it)
    }
}

class AnimationRef() {
    var animation: Animation? = null

    fun get() = animation ?: error("Анимация не создана")

    fun set(a: Animation) {
        animation = a
    }
}

typealias AnimationBehaviour = Draw2dContext.(View, Float) -> Unit

@UiDsl
class AssociatedAnimationScope(internal val viewScope: ViewScope, private val animationScope: AnimationScope) : AnimationBuilder by animationScope {
    private var playImmediately = false
    private var playOnHover: PlayOnHoverNode? = null
    private var onUpdate: AnimationBehaviour = { _, _ -> }
    private var onStart: (View, Animation) -> Unit = { _, _ ->}
    private var onEnd: (View, Animation) -> Unit = { _, _ -> }
    private var onCancel: (Animation, View) -> Unit = { _, _ ->}

    fun playImmediately() {
        playImmediately = true
    }

    fun hover(builder: HoverScope.() -> Unit) {
        playOnHover = HoverScope().apply { builder() }.build()
    }

    @UiDsl
    inner class HoverScope() {
        private var onHoverStart: (View, Animation) -> Unit = { _, _, -> }
        private var onHoverEnd: (View, Animation) -> Unit = { _, _, -> }

        fun start(behaviour: (View, Animation) -> Unit) {
            onHoverStart = behaviour
        }

        fun end(behaviour: (View, Animation) -> Unit) {
            onHoverEnd = behaviour
        }


        internal fun build(): PlayOnHoverNode {
            return PlayOnHoverNode(onHoverStart, onHoverEnd)
        }
    }

    fun onCancel(behaviour: (Animation, View) -> Unit) {
        onCancel = behaviour
    }

    fun onUpdate(behaviour: AnimationBehaviour) {
        onUpdate = behaviour
    }

    fun onStart(behaviour: (View, Animation) -> Unit) {
        onStart = behaviour
    }

    fun onEnd(behaviour: (View, Animation) -> Unit) {
        onEnd = behaviour
    }

    internal fun buildInView(): ViewAnimationNode {
        val view by lazy { viewScope.futureReference.get() }
        val updateBehaviour = onUpdate
        val startBehaviour = onStart
        val endBehaviour = onEnd
        val cancelBehaviour = onCancel
        val animation = animationScope.apply {
            onUpdate { value ->
                val ctx = view.require<AnimationManager>().currentDrawContext
                ctx?.updateBehaviour(view, value)
            }
            onStart {
                startBehaviour.invoke(view, animationReference.get())
            }
            onEnd {
                endBehaviour.invoke(view, animationReference.get())
            }
            onCancel {
                cancelBehaviour.invoke(animationReference.get(), view)
            }
        }.build()
        return ViewAnimationNode(animation, playImmediately, playOnHover)
    }
}

internal data class ViewAnimationNode(val animation: Animation, val playImmediately: Boolean, val playOnHover: PlayOnHoverNode?)

internal data class PlayOnHoverNode(val onHoverStrategy: (View, Animation) -> Unit, val onEndHoverStrategy: (View, Animation) -> Unit)

@UiDsl
class ViewAnimationScope(private val viewScope: ViewScope) {
    private val animations: MutableList<ViewAnimationNode> = mutableListOf()

    fun animation(builder: AssociatedAnimationScope.() -> Unit) {
        animations += AssociatedAnimationScope(viewScope, AnimationScope()).apply { builder() }.buildInView()
    }

    internal fun build() {
        val animations = animations
        viewScope.apply {
            val futureView by lazy { futureReference.get() }
            component { ctx ->
                AnimationManager(ctx).apply {
                    animations.forEach { a ->
                        addAnimation(a.animation, false)
                    }
                }
            }

            animations.forEach { a ->
                val hover = a.playOnHover
                if (hover != null) {
                    component { ctx ->
                        OnHover(
                            ctx,
                            object : HoverListener {
                                override fun onHoverStart(component: ViewComponent) {
                                    a.playOnHover.onHoverStrategy(futureView, a.animation)
                                }

                                override fun onHoverEnd(component: ViewComponent) {
                                    a.playOnHover.onEndHoverStrategy(futureView, a.animation)
                                }
                            },
                        )
                    }
                }
            }

            post {
                animations.forEach { a -> if (a.playImmediately) a.animation.start() }
            }
        }
    }
}

@UiDsl
fun ViewScope.animate(builder: ViewAnimationScope.() -> Unit) {
    ViewAnimationScope(this).apply { builder() }.build()
}