package org.qbrp.client.render.ui.dsl

import org.qbrp.client.render.ui.UiContext
import org.qbrp.client.render.ui.components.behaviour.HighlightParams
import org.qbrp.client.render.ui.components.behaviour.OnHover
import org.qbrp.client.render.ui.components.dataview.CellSpec
import org.qbrp.client.render.ui.components.dataview.ColumnType
import org.qbrp.client.render.ui.components.dataview.DataGrid
import org.qbrp.client.render.ui.components.dataview.DataTable
import org.qbrp.client.render.ui.components.dataview.DataGridClickListener
import org.qbrp.client.render.ui.components.dataview.GridHighlighter
import org.qbrp.client.render.ui.components.dataview.RowSpec
import org.qbrp.client.render.ui.components.dataview.RowStyle
import org.qbrp.client.render.ui.components.dataview.TableAdapter
import org.qbrp.client.render.ui.components.dataview.TableRowClickListener
import org.qbrp.client.render.ui.components.layout.grid.GradientLineDrawer
import org.qbrp.client.render.ui.components.layout.grid.GridLines
import org.qbrp.client.render.ui.components.layout.grid.LineDrawer
import org.qbrp.client.render.ui.view.View
import org.qbrp.main.core.components.extensions.require

@UiDsl
class DataGridBuilder<S : Any>(private val viewScope: ViewScope) {
    private var adapter: TableAdapter<S>? = null
    private var gridScopeBuilder: (GridScope.() -> Unit)? = null
    private var columnGap = 0f
    private var rowGap = 0f

    fun adapter(t: TableAdapter<S>) {
        adapter = t
    }

    fun table(table: DataTable<S>, builder: TableAdapterBuilder<S>.() -> Unit) {
        adapter(TableAdapterBuilder<S>().apply { builder() }.buildSnapshotAdapter(table))
    }

    fun grid(builder: GridScope.() -> Unit) {
        gridScopeBuilder = builder
    }

    fun highlight(params: HighlightParams = HighlightParams.DEFAULT) {
        viewScope.component(ViewScope.HIGHEST) { ctx -> OnHover(ctx, GridHighlighter(params, ctx.require())) }
    }

    fun lines(drawer: LineDrawer) {
        viewScope.component(ViewScope.HIGH) { ctx -> GridLines(ctx, drawer) }
    }

    fun gradientLines(startColor: Int, endColor: Int) {
        lines(GradientLineDrawer(startColor, endColor))
    }

    fun gaps(row: Float = 0f, column: Float = 0f) {
        rowGap = row
        columnGap = column
    }

    context(ui: UiContext)
    internal fun build() {
        val gridScope = gridScopeBuilder
        val adapter = adapter!!
        val rowGap = rowGap
        val columnGap = columnGap
        viewScope.apply {
            grid {
                gridScope?.let { it() }
                rows {
                    min(0)
                    gap(rowGap)
                }
                columns {
                    count(adapter.getColumnCount())
                    gap(columnGap)
                }
            }
            component(ViewScope.HIGHEST) { ctx ->
                DataGrid(ctx, adapter)
                    .apply { invalidate() }
            }
            component { ctx ->
                DataGridClickListener(ctx, ctx.require(), ctx.require<DataGrid<S>>(), ui)
            }
        }
    }
}

@UiDsl
context(ui: UiContext)
fun <S: Any> ViewScope.datagrid(builder: DataGridBuilder<S>.() -> Unit) {
    DataGridBuilder<S>(this).apply { builder() }.build()
}

fun <S: Any> TableAdapterBuilder<S>.buildSnapshotAdapter(dataTable: DataTable<S>): SnapshotTableAdapter<S> {
    val viewColumns = viewColumns.toList()
    val adapter = SnapshotTableAdapter(dataTable) { row ->
        val rowView = dataTable.rowAt(row)
        val cells = viewColumns
            .associate { viewColumn ->
                viewColumn as TableAdapterBuilder.ViewColumn<Any>
                (viewColumn.type) to (rowView.getOrNull(viewColumn.type)?.let { viewColumn.render(it) } ?: View())
            }
            .mapValues { (type, view) -> CellSpec(view) }
        val source = dataTable.rowAt(row).source
        val style = rowStyleProvider.invoke(row, source.data)
        val clickListener = rowClickListener
        RowSpec(source, cells, style, clickListener)
    }
    return adapter
}

typealias RowStyleProvider<S> = (Int, S) -> RowStyle

@UiDsl
class TableAdapterBuilder<S: Any>() {
    internal var viewColumns: MutableList<ViewColumn<out Any>> = mutableListOf()
    internal var rowStyleProvider: RowStyleProvider<S> = { _, _ -> RowStyle.DEFAULT }
    internal var rowClickListener: TableRowClickListener<S>? = null

    @JvmName("defaultColumnBuilder")
    fun <T: Any> column(type: ColumnType<T>, builder: ColumnBuilder<T>.() -> Unit) {
        ColumnBuilder(type).apply { builder() }.build()
    }

    fun <T: Any> column(type: ColumnType<T>, render: (T) -> View) {
        viewColumns += ViewColumn<T>(type, render)
    }

    fun rowStyle(provider: RowStyleProvider<S>) {
        this.rowStyleProvider = provider
    }

    fun onClickRow(listener: TableRowClickListener<S>) {
        rowClickListener = listener
    }

    inner class ColumnBuilder<T: Any>(val type: ColumnType<T>) {
        private var viewGetter: ((T) -> View)? = null

        fun render(getter: (T) -> View) {
            viewGetter = getter
        }

        fun build() {
            viewColumns += ViewColumn<T>(type, viewGetter!!)
        }
    }

    data class ViewColumn<T: Any>(val type: ColumnType<T>, val render: (T) -> View)
}

class SnapshotTableAdapter<S: Any>(val table: DataTable<S>, val rowSpecGetter: (Int) -> RowSpec<S>): TableAdapter<S> {
    override val isDirty: Boolean = false
    override fun getRowSpec(row: Int): RowSpec<S> = rowSpecGetter(row)
    override fun getColumnCount(): Int = table.columnsCount
    override fun getRowCount(): Int = table.rowsCount
}