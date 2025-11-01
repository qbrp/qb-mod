package org.qbrp.client.render.ui.interaction.list

import org.qbrp.client.core.EngineId
import org.qbrp.client.core.mouse.MouseState
import org.qbrp.client.controls.ClientInteractionManager
import org.qbrp.client.render.ui.UiContext
import org.qbrp.client.render.ui.components.behaviour.CloseListener
import org.qbrp.client.render.ui.components.behaviour.CloseOn
import org.qbrp.client.render.ui.components.behaviour.MouseClickedOutView
import org.qbrp.client.render.ui.components.dataview.PopupMenuItem
import org.qbrp.client.render.ui.components.window.Views
import org.qbrp.client.render.ui.dsl.IconPrefab
import org.qbrp.client.render.ui.dsl.ItemControlBuilder
import org.qbrp.client.render.ui.dsl.UiDsl
import org.qbrp.client.render.ui.dsl.ViewBuildContinue
import org.qbrp.client.render.ui.dsl.ViewScope
import org.qbrp.client.render.ui.dsl.buildView
import org.qbrp.client.render.ui.dsl.finish
import org.qbrp.client.render.ui.dsl.icon
import org.qbrp.client.render.ui.dsl.toolstrip
import org.qbrp.client.render.ui.dsl.viewOf
import org.qbrp.client.render.ui.interaction.InteractionsViewModule
import org.qbrp.main.engine.Colors
import org.qbrp.main.engine.controls.dsl.InteractionDSL
import org.qbrp.main.engine.controls.entity.display.InteractableViewData
import org.qbrp.main.engine.controls.entity.display.InteractionAvailability
import org.qbrp.main.engine.controls.entity.display.InteractionContextMenuViewData
import org.qbrp.main.engine.controls.entity.display.InteractionIcon
import org.qbrp.main.engine.controls.entity.display.InteractionViewNode
import org.qbrp.main.engine.text.literal
import org.qbrp.main.engine.text.style.Style
import org.qbrp.main.engine.text.style.TextColor
import org.qbrp.main.engine.tooltip.TooltipColorProfile
import org.qbrp.main.engine.tooltip.TooltipViewData
import org.qbrp.main.engine.tooltip.component.PlainTextTooltipComponent
import org.qbrp.main.engine.tooltip.component.TooltipComponent

class InteractionListFactory(
    private val interactionManager: ClientInteractionManager,
) {

    @InteractionDSL
    context(ctx: UiContext)
    private fun ViewScope.objectSelection(obj: InteractableViewData) {
        view {
            horizontal(6f)
            view {
                icon(interactionIconToIconPrefab(obj.icon), 16f)
            }
            view {
                verticalAlignment()
                text(obj.name.text)
            }
        }
    }

    context(ctx: UiContext)
    fun create(
        contextMenuData: InteractionContextMenuViewData,
        closeListener: CloseListener,
        colorProfile: TooltipColorProfile
    ) : AppliedInteractionList {
        val hasInteractions = contextMenuData.isNotEmpty()
        val interactionsByObj = contextMenuData.objectInteractions
        val objects = interactionsByObj
            .map { it.obj }
        val objectInteractionViews = interactionsByObj
            .map { interactionListView(it.nodes, closeListener, colorProfile, InteractionsViewModule.Companion.VIEW_ID) }
        val views = Views(objectInteractionViews)

        val panel = interactionPanel(colorProfile) {
            when(hasInteractions) {
                true -> {
                    view {
                        horizontal(6f)
                        objects.forEach { objectSelection(it) }
                    }
                    viewManager(views) //список интеракций, менеджер окон списка взаимодействий для разных объектов
                }

                false -> view {
                    text("Взаимодействий нет")
                    component { CloseOn(it, MouseClickedOutView(it)) }
                }
            }
        }.finish()

        return AppliedInteractionList(panel, !hasInteractions)
    }

    context(ctx: UiContext)
    internal fun interactionListView(
        interactions: List<InteractionViewNode>,
        closeListener: CloseListener,
        colorProfile: TooltipColorProfile,
        id: String
    ) = viewOf {
        interactionList(interactions, closeListener, colorProfile, id)
    }

    @UiDsl
    context(ctx: UiContext)
    internal fun ViewScope.interactionList(
        interactions: List<InteractionViewNode>,
        closeListener: CloseListener,
        colorProfile: TooltipColorProfile,
        id: String
    ) {
        matchParent()
        toolstrip {
            parent(id)
            onClose(closeListener)
            gap(8f)
            root {
                zIndex(500)
                tooltipBackground(colorProfile)
            }

            interactions.forEach { interactionItem(it) }
        }
    }

    @UiDsl
    internal fun ItemControlBuilder.interactionItem(i: InteractionViewNode) {
        item {
            label(i.label)
            i.icon?.let { icon(interactionIconToIconPrefab(it)) }
            i.children.forEach { interactionItem(it) }
            i.description.let { if (it.isNotEmpty()) tooltip(InteractionTooltip(it)) }
            i.interaction?.let { interaction ->
                val availability = interaction.availability
                if (availability is InteractionAvailability.No) {
                    val rootLiteral = literal("Взаимодействие недоступно", Style(TextColor.Companion.RED))
                    val reasonLiterals = availability.reasons.map { literal("- $it", Style(TextColor.Companion.RED)) }
                    tooltip(
                        InteractionFailureTooltip(
                            listOf(
                                PlainTextTooltipComponent(listOf(rootLiteral) + reasonLiterals)
                            )
                        )
                    )
                    style(PopupMenuItem.Style(Colors.RED_DIM))
                } else {
                    onClickItem(InteractionClickListener(interaction.id, interactionManager))
                }
            }
        }
    }

    class InteractionTooltip(override val components: List<TooltipComponent>) : TooltipViewData

    class InteractionFailureTooltip(override val components: List<TooltipComponent>) : TooltipViewData

    internal fun interactionIconToIconPrefab(icon: InteractionIcon): IconPrefab {
        return when(icon) {
            is InteractionIcon.Asset -> IconPrefab.Asset(EngineId(icon.id.value))
            is InteractionIcon.Item -> IconPrefab.Item(icon.id)
            is InteractionIcon.Player -> IconPrefab.PlayerHead(icon.id)
        }
    }

    @UiDsl
    context(ui: UiContext)
    internal fun interactionPanel(colors: TooltipColorProfile, build: ViewBuildContinue) = buildView {
        interactionPanel(colors)
        use(build)
    }

    @UiDsl
    context(ui: UiContext)
    internal fun ViewScope.interactionPanel(colors: TooltipColorProfile) {
        vertical(2f)
        zIndex(500)
        overlayBehaviour()
        resolveAnchorPosition(ui.mouse)
        tooltipBackground(colors)
    }


    @UiDsl
    internal fun ViewScope.resolveAnchorPosition(mouse: MouseState) {
        when(mouse.isLocked) {
            true -> anchors(0.05f, 0.05f)
            false -> anchorCursor(mouse)
        }
    }
}