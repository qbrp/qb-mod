package org.qbrp.client.render.ui

import org.qbrp.client.render.ui.components.animation.AnimationManager
import org.qbrp.client.render.ui.components.behaviour.CloseOn
import org.qbrp.client.render.ui.components.behaviour.Translation
import org.qbrp.client.render.ui.components.behaviour.Draggable
import org.qbrp.client.render.ui.components.behaviour.MouseClickedOutView
import org.qbrp.client.render.ui.components.behaviour.OnClick
import org.qbrp.client.render.ui.components.behaviour.OnClose
import org.qbrp.client.render.ui.components.behaviour.OnHover
import org.qbrp.client.render.ui.components.dataview.ControlViewMark
import org.qbrp.client.render.ui.components.dataview.DataGrid
import org.qbrp.client.render.ui.components.dataview.DataGridClickListener
import org.qbrp.client.render.ui.components.dataview.ItemControlComponent
import org.qbrp.client.render.ui.components.dataview.PopupItemComponent
import org.qbrp.client.render.ui.components.effect.Blend
import org.qbrp.client.render.ui.components.effect.Cutout
import org.qbrp.client.render.ui.components.item.Item
import org.qbrp.client.render.ui.inventory.components.Slot
import org.qbrp.client.render.ui.components.layout.FixedSize
import org.qbrp.client.render.ui.components.layout.LayoutComponent
import org.qbrp.client.render.ui.components.layout.grid.AutoGridLayout
import org.qbrp.client.render.ui.components.layout.grid.GridLines
import org.qbrp.client.render.ui.components.layout.grid.GridPosition
import org.qbrp.client.render.ui.components.layout.grid.OnRowClick
import org.qbrp.client.render.ui.inventory.components.SlotGrid
import org.qbrp.client.render.ui.components.text.Text
import org.qbrp.client.render.ui.components.texture.Background
import org.qbrp.client.render.ui.components.texture.PlayerHeadTexture
import org.qbrp.client.render.ui.components.texture.Texture
import org.qbrp.client.render.ui.components.tooltip.Tooltip
import org.qbrp.client.render.ui.components.tooltip.TooltipBackground
import org.qbrp.client.render.ui.components.transform.RectTransform
import org.qbrp.client.render.ui.components.transform.RootLayout
import org.qbrp.client.render.ui.components.transform.old.HorizontalCentralisation
import org.qbrp.client.render.ui.components.transform.old.VerticalCentralisation
import org.qbrp.client.render.ui.components.window.WindowManager
import org.qbrp.client.render.ui.view.ViewGroup
import org.qbrp.main.core.components.ComponentRegistrar
import org.qbrp.main.core.components.ComponentRegistry

class UiComponentRegistrar(private val registrar: ComponentRegistrar) {
    constructor(registry: ComponentRegistry): this(ComponentRegistrar(registry))
    fun registerAll() = registrar.apply {
        use<ControlViewMark>()
        use<Draggable>()
        use<DataGrid<*>>()
        use<PopupItemComponent>()
        use<ItemControlComponent>()
        use<DataGridClickListener<*>>()
        use<ViewGroup.Member>()
        use<TooltipBackground>()
        use<LayoutComponent>()
        use<FixedSize>()
        use<VerticalCentralisation>()
        use<Translation>()
        use<HorizontalCentralisation>()
        use<AnimationManager>()
        use<Cutout>()
        use<AutoGridLayout>()
        use<OnClose>()
        use<GridPosition>()
        use<GridLines>()
        use<Blend>()
        use<Text>()
        use<OnRowClick>()
        use<CloseOn>()
        use<MouseClickedOutView>()
        use<PlayerHeadTexture>()
        use<Texture>()
        use<WindowManager>()
        use<Item>()
        use<Background>()
        use<OnClick>()
        use<RectTransform>()
        use<RootLayout>()
        use<SlotGrid>()
        use<Slot>()
        use<OnHover>()
        use<Tooltip>()
    }
}