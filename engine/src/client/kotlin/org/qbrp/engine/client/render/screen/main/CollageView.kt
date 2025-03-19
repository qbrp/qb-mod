package org.qbrp.engine.client.render.screen.main

import icyllis.modernui.core.Context
import icyllis.modernui.graphics.Image
import icyllis.modernui.graphics.drawable.Drawable
import icyllis.modernui.widget.ImageView
import java.util.Timer
import kotlin.concurrent.timerTask

class CollageView(
    context: Context,
    private val images: Array<Image>, // Массив изображений
    private val interval: Long = 2000,  // Интервал между сменой изображений (в миллисекундах)
    private val animation: (ImageView.(oldImage: Image, newImage: Image) -> Unit)? = null // Лямбда для анимации
) : ImageView(context) {

    private var currentIndex = 0
    private var timer: Timer? = null

    // Событие смены изображения
    var onImageChanged: ((oldImage: Image, newImage: Image) -> Unit)? = null

    init {
        if (images.isNotEmpty()) {
            setImage(images[currentIndex]) // Устанавливаем первое изображение
            startAnimationLoop()
        }
    }

    // Запуск цикла смены изображений
    private fun startAnimationLoop() {
        timer = Timer().apply {
            scheduleAtFixedRate(timerTask {
                val oldImage = images[currentIndex]
                currentIndex = (currentIndex + 1) % images.size // Переход к следующему изображению
                val newImage = images[currentIndex]

                onImageChanged?.invoke(oldImage, newImage)

                post {
                    animation?.let { anim ->
                        anim(this@CollageView, oldImage, newImage)
                    } ?: setImage(newImage) // Если анимация не задана, просто меняем изображение
                }
            }, interval, interval)
        }
    }

    // Остановка таймера (например, при уничтожении View)
    fun stopAnimationLoop() {
        timer?.cancel()
        timer = null
    }

    // Установка нового массива изображений
    fun setImages(newImages: Array<Image>) {
        stopAnimationLoop()
        this.images.indices.forEach { i ->
            if (i < newImages.size) {
                images[i] = newImages[i]
            }
        }
        currentIndex = 0
        setImage(images[currentIndex])
        startAnimationLoop()
    }
}