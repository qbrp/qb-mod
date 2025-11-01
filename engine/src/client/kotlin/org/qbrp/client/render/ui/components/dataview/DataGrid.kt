package org.qbrp.client.render.ui.components.dataview

import org.qbrp.client.render.Drawable
import org.qbrp.client.render.dataformats.Vec2
import org.qbrp.client.render.dataformats.vec2
import org.qbrp.client.render.screen.context.Draw2dContext
import org.qbrp.client.render.ui.UiContext
import org.qbrp.client.render.ui.components.behaviour.HighlightParams
import org.qbrp.client.render.ui.components.behaviour.Highlighter
import org.qbrp.client.render.ui.components.behaviour.HoverListener
import org.qbrp.client.render.ui.components.behaviour.ProgressMode
import org.qbrp.client.render.ui.components.event.Clickable
import org.qbrp.client.render.ui.components.layout.grid.AutoGridLayout
import org.qbrp.client.render.ui.components.layout.grid.GridPosition
import org.qbrp.client.render.ui.components.transform.RectTransform
import org.qbrp.client.render.ui.view.View
import org.qbrp.client.render.ui.view.ViewComponent
import org.qbrp.client.render.ui.view.ViewContext
import org.qbrp.main.core.components.extensions.add
import org.qbrp.main.core.components.extensions.require
import kotlin.reflect.KClass

class DataGrid<S : Any>(
    ctx: ViewContext,
    val table: TableAdapter<S>
) : ViewComponent(ctx), Drawable {
    private val transform = view.require<RectTransform>()

    override fun onTransform(ctx: Draw2dContext) {
        if (table.isDirty) invalidate()
    }

    override fun onRender(ctx: Draw2dContext) {
        repeat(table.getRowCount()) { row ->
            val style = table.getRowSpec(row).style
            if (style.background != null) {
                ctx.fill(0f, 0f, view.width, view.height, style.background)
            }
        }
    }

    fun invalidate() {
        view.removeAllViews()
        for (row in 0 until table.getRowCount()) {
            val rowSpec = table.getRowSpec(row)
            val cells = rowSpec.cells
            for (cell in 0 until table.getColumnCount()) {
                val cellView = cells.values.toList()[cell].view
                cellView.add(GridPosition(cellView.componentContext, cell, row))
                view.addView(cellView)
            }
        }
        transform.invalidateMeasure()
    }
}

class DataGridClickListener<S : Any>(
    ctx: ViewContext,
    private val autoGridLayout: AutoGridLayout,
    private var dataGrid: DataGrid<S>,
    private val uiContext: UiContext
) : ViewComponent(ctx), Clickable {
    override fun onClick(button: Int, lx: Float, ly: Float): Boolean = with(uiContext) {
        autoGridLayout.elemAt(lx, ly)?.let {
            val rowIndex = it.gp.row
            val row = dataGrid.table.getRowSpec(rowIndex)
            val clickListener = row.clickListener
            clickListener?.onClick(button, vec2(lx, ly), row, dataGrid)
        }
            ?: false
    }
}

class GridHighlighter(
    params: HighlightParams,
    private val layout: AutoGridLayout
) : HoverListener {
    private val highlighter = Highlighter(params)
    private var hoveredRow: Int = 0
    private var hovered: Boolean = false

    override fun render(ctx: Draw2dContext, view: View) {
        if (!hovered) return
        val row = hoveredRow
        val size = layout.rowSize(row)
        val elem = layout.elemsAtRow(row).firstOrNull() ?: return
        highlighter.render(ctx, 0f, elem.view.y, view.width, size.h)
    }

    override fun onHover(view: View, component: ViewComponent) {
        highlighter.updateProgress(ProgressMode.INCREASE)
    }

    override fun onNotHover(ctx: Draw2dContext, view: View) {
        highlighter.updateProgress(ProgressMode.DECREASE)
    }

    override fun onHoverStart(component: ViewComponent) {
        hovered = true
    }

    override fun onHoverEnd(component: ViewComponent) {
        hovered = false
    }

    override fun handleMouseOnHover(lx: Float, ly: Float, view: View) {
        val row = layout.elemAt(lx, ly)?.gp?.row
        row?.let { hoveredRow = it }
    }
}

// Behaviour

fun interface TableRowClickListener<S: Any> {
    context(ui: UiContext) fun onClick(button: Int, pos: Vec2, row: RowSpec<S>, grid: DataGrid<S>): Boolean
}

// Render

class CellSpec(val view: View)

data class RowSpec<S: Any>(
    val source: DataSource<S>,
    val cells: Map<ColumnType<Any>, CellSpec>,
    val style: RowStyle = RowStyle.DEFAULT,
    val clickListener: TableRowClickListener<S>? = null
)

data class RowStyle(val background: Int? = null) {
    companion object {
        val DEFAULT = RowStyle(null)
    }
}

interface TableAdapter<S: Any> {
    fun getRowSpec(row: Int): RowSpec<S>
    fun getColumnCount(): Int
    fun getRowCount(): Int
    val isDirty: Boolean
}

// Data

data class ColumnType<T: Any>(val id: String, val type: KClass<T>)

data class DataSource<S: Any>(val data: S)

data class Column<T: Any, S: Any>(val type: ColumnType<T>, val dataGetter: (S) -> T)

open class Table<S: Any> {
    private val _columns = mutableListOf<Column<out Any, S>>()
    val columns: List<Column<out Any, S>> get() = _columns
    val columnsCount get() = columns.count()

    protected fun <T: Any> column(id: String, kClass: KClass<T>, dataGetter: (S) -> T): ColumnType<T> {
        val column = Column(ColumnType(id, kClass), dataGetter)
        _columns.add(column)
        return column.type
    }

    protected inline fun <reified T: Any> column(id: String, noinline dataGetter: (S) -> T): ColumnType<T> {
        return column(id, T::class, dataGetter)
    }

    @Suppress("UNCHECKED_CAST")
    fun <T: Any> getColumn(type: ColumnType<T>): Column<T, S> {
        return columns.find { it.type == type } as Column<T, S>
    }
}

class DataTable<S: Any> internal constructor(val table: Table<S>, val rows: List<DataSource<S>>) {
    val rowsCount get() = rows.count()
    val columnsCount get() = table.columnsCount

    fun rowAt(index: Int): Row<S> {
        return Row(table, rows[index])
    }

    class Row<S: Any> internal constructor(private val table: Table<S>, val source: DataSource<S>) {
        fun <T: Any> getOrNull(type: ColumnType<T>): T? {
            return table.getColumn(type).dataGetter.invoke(source.data)
        }

        fun <T: Any> get(column: ColumnType<T>): T {
            return getOrNull(column) ?: throw ColumnUndefinedException(column)
        }
    }
}

fun <S: Any> Table<S>.withDataSource(sources: List<S>): DataTable<S> {
    val sourceList = sources.map { DataSource(it) }
    return DataTable(this, sourceList)
}

fun <S: Any> Table<S>.withEmptyData(): DataTable<S> {
    return withDataSource(listOf())
}

class ColumnUndefinedException(columnType: ColumnType<*>): RuntimeException("Column $columnType not defined")

class Row(val slots: Map<ColumnType<*>, Any?>)
