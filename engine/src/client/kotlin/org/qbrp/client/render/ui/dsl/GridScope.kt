package org.qbrp.client.render.ui.dsl

import org.qbrp.client.render.ui.components.behaviour.HighlightParams
import org.qbrp.client.render.ui.components.layout.grid.OnRowClick
import org.qbrp.client.render.ui.components.layout.grid.GridRowClickListener
import org.qbrp.main.core.components.extensions.require

@UiDsl
fun ViewScope.grid(m: GridScope.() -> Unit) {
    layout = GridScope(this).apply { m() }.build()
}

@UiDsl
class GridScope(private val view: ViewScope) {
    private val cells = mutableListOf<CellNode>()
    private var rowCfg = RowCfg()
    private var colCfg = ColCfg()

    fun rows(block: RowsBuilder.() -> Unit) {
        rowCfg = RowsBuilder(view).apply(block).build()
    }

    fun columns(block: ColsBuilder.() -> Unit) {
        colCfg = ColsBuilder().apply(block).build()
    }

    fun cell(r: Int, c: Int, block: ViewScope.() -> Unit) {
        val child = ViewScope().apply(block).build()
        cells += CellNode(r, c, child)
    }

    internal fun build(): AutoGridNode = AutoGridNode(
        cols = colCfg.count,
        colsGap = colCfg.gap,
        rowGap = rowCfg.gap,
        colMin = colCfg.mins?.toFloatArray(),
        cells = cells
    )
}

data class RowCfg(
    val gap: Float = 0f,
)

@UiDsl
class RowsBuilder(private val view: ViewScope) {
    private var gap: Float = 0f
    private var minRows: Int? = null

    fun onClick(next: Boolean = false, listener: GridRowClickListener) {
        view.component { OnRowClick(it, listener, it.require(), next) }
    }

    fun onClick(listener: GridRowClickListener) {
        onClick(false, listener)
    }

    fun min(count: Int) { minRows = count }

    fun gap(value: Float) { gap = value }

    fun build(): RowCfg {
        return RowCfg(gap = gap)
    }
}

data class ColCfg(
    val count: Int = 0,
    val gap: Float = 0f,
    val mins: List<Float>? = null
)

@UiDsl
class ColsBuilder {
    private var count: Int = 0
    private var gap: Float = 0f
    private val mins = mutableListOf<Float>()

    fun gap(value: Float) { gap = value }
    fun min(vararg values: Float) { mins.clear(); mins.addAll(values.asList()) }
    fun count(count: Int) { this.count = count } // опционально, если хочешь задавать тут

    fun build(): ColCfg = ColCfg(count = count, gap = gap, mins = if (mins.isEmpty()) null else mins)
}