package org.qbrp.engine.client.render.screen.main

import icyllis.modernui.animation.Animator
import icyllis.modernui.animation.AnimatorListener
import icyllis.modernui.animation.ValueAnimator
import icyllis.modernui.fragment.Fragment
import icyllis.modernui.graphics.BitmapFactory
import icyllis.modernui.graphics.Image
import icyllis.modernui.util.DataSet
import icyllis.modernui.view.LayoutInflater
import icyllis.modernui.view.View
import icyllis.modernui.view.ViewGroup
import icyllis.modernui.widget.FrameLayout
import icyllis.modernui.widget.ImageView
import net.minecraft.client.render.entity.animation.AnimationHelper.animate
import net.minecraft.util.Identifier
import org.qbrp.core.Core
import org.qbrp.engine.client.core.resources.ClientResources
import java.io.File
import java.nio.file.Files
import javax.imageio.ImageIO

class MainMenuScreen: Fragment() {
    companion object {
        const val FADE_ANIMATION_DURATION = 1000L
    }
    val imageDescriptions = ClientResources.root.getMenuImageDescriptions().toTypedArray()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: DataSet?
    ): View? {
        val rootLayout = FrameLayout(requireContext()).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }

        val images = imageDescriptions.map { element -> ClientResources.root.getMenuImage(element.imageId) }.toTypedArray()

        val collageView = CollageView(requireContext(), images, interval = 3000) { oldImage, newImage ->
            // Устанавливаем новое изображение сразу
            setImage(newImage)

            // Анимация исчезновения старого изображения
            val animator = ValueAnimator.ofFloat(1f, 0f).apply {
                duration = FADE_ANIMATION_DURATION
                addUpdateListener { animation ->
                    this@CollageView.alpha = animation.animatedValue as Float
                }
                addListener(object : AnimatorListener {
                    override fun onAnimationStart(animation: Animator) {}
                    override fun onAnimationEnd(animation: Animator) {
                        // Возвращаем прозрачность к исходному значению
                        this@CollageView.alpha = 1f
                    }
                    override fun onAnimationCancel(animation: Animator) {}
                    override fun onAnimationRepeat(animation: Animator) {}
                })
            }

            animator.start()
        }
        rootLayout.addView(collageView)
        return rootLayout
    }

}