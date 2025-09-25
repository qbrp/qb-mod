package org.qbrp.client.render.ui.components.behaviour

import org.qbrp.client.render.Drawable
import org.qbrp.client.render.dataformats.Vec2
import org.qbrp.client.render.dataformats.vec2
import org.qbrp.client.render.screen.context.Draw2dContext
import org.qbrp.client.render.ui.components.event.Clickable
import org.qbrp.client.render.ui.components.event.MouseDrag
import org.qbrp.client.render.ui.components.event.MouseRelease
import org.qbrp.client.render.ui.components.layout.fixAnchorsAtParentPivot
import org.qbrp.client.render.ui.components.transform.RectTransform
import org.qbrp.client.render.ui.view.View
import org.qbrp.client.render.ui.view.ViewComponent
import org.qbrp.client.render.ui.view.ViewContext
import org.qbrp.main.core.components.extensions.getOrAdd
import org.qbrp.main.core.components.extensions.remove
import org.qbrp.main.core.components.extensions.require

class Translation(ctx: ViewContext, var vec: Vec2): ViewComponent(ctx), Drawable {
    override fun initRender(ctx: Draw2dContext) {
        ctx.matrices.translate(vec.x, vec.y, 0f)
    }
}


open class Draggable(
    ctx: ViewContext,
    private val moveView: View = ctx.parent,
    private val transform: RectTransform = moveView.require(),
    var onStartDrag: () -> Unit = {},
    var onReleaseDrag: () -> Unit = {}
) : ViewComponent(ctx), Clickable, MouseDrag, Drawable, MouseRelease {

    private var dragging = false
    private var lastX = 0f        // абсолютная позиция курсора в момент клика
    private var lastY = 0f
    private var deltaX = 0f       // текущий визуальный сдвиг (через матрицу)
    private var deltaY = 0f

    override fun onClick(button: Int, lx: Float, ly: Float): Boolean {
        if (button != 0) return false
        dragging = true

        // запоминаем абсолютную позицию курсора: позиция self + локальная точка клика
        val self = transform.lastSelfRect
        lastX = self.x + lx
        lastY = self.y + ly

        // начинаем без сдвига
        deltaX = 0f
        deltaY = 0f

        onStartDrag()

        moveView.bringToFront()
        view.bringToFront()
        return true
    }

    override fun onMouseDragged(lmX: Float, lmY: Float) {
        if (!dragging) return

        // текущая абсолютная позиция курсора: базовый rect (без матричного сдвига) + локальные координаты события
        val self = transform.lastSelfRect
        val curAbsX = self.x + lmX
        val curAbsY = self.y + lmY

        // общий сдвиг с момента клика
        deltaX = curAbsX - lastX
        deltaY = curAbsY - lastY
    }

    override fun onFrame() {
        if (deltaX == 0f && deltaY == 0f) return

        val d = vec2(deltaX, deltaY)
        moveView.getOrAdd { Translation(moveView.componentContext, d) }.vec = d
    }

    override fun onMouseRelease(lmX: Float, lmY: Float, button: Int): Boolean {
        if (button == 0 && dragging) {
            dragging = false
            onReleaseDrag()

            if (deltaX != 0f || deltaY != 0f) {
                val s = transform.lastSelfRect

                // куда хотим поместить pivot после драга (в системе родителя):
                val targetPivotAbsX = (s.x + deltaX) + transform.anchors.pivot.x * s.w
                val targetPivotAbsY = (s.y + deltaY) + transform.anchors.pivot.y * s.h

                // фиксируем якоря по ручным абсолютным координатам pivot
                transform.fixAnchorsAtParentPivot(targetPivotAbsX, targetPivotAbsY, clearMargin = true)

                // сбрасываем временный визуальный сдвиг и перестраиваем
                deltaX = 0f
                deltaY = 0f
                moveView.remove<Translation>()
                transform.invalidateArrange()
            }
            return true
        }
        return false
    }
}
