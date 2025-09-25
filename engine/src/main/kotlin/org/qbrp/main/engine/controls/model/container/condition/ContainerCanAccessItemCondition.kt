package org.qbrp.main.engine.controls.model.container.condition

import org.qbrp.main.engine.containers.util.ContainerId
import org.qbrp.main.engine.containers.v2.api.ContainerService
import org.qbrp.main.engine.controls.entity.conditions.AvailableResult
import org.qbrp.main.engine.controls.entity.conditions.Condition
import org.qbrp.main.engine.controls.entity.interactable.Interactable
import org.qbrp.main.engine.controls.entity.interactions.InteractionContext
import org.qbrp.main.engine.controls.model.item.ItemInteractable

typealias ItemProvider<Subject, Object> = (Subject, Object, InteractionContext) -> ItemInteractable
typealias ContainerProvider<Subject, Object> = (Subject, Object, InteractionContext) -> List<ContainerId>
typealias SingleContainerProvider<Subject, Object> = (Subject, Object, InteractionContext) -> ContainerId?

class ContainerCanAccessItemCondition<Subject: Interactable, Object: Interactable>(private val containerService: ContainerService,
                                                                                   val containerProvider: ContainerProvider<Subject, Object>,
                                                                                   val itemProvider: ItemProvider<Subject, Object>): Condition<Subject, Object> {

    constructor(singleContainerProvider: SingleContainerProvider<Subject, Object>,
                itemProvider: ItemProvider<Subject, Object>,
                service: ContainerService,): this(
        service,
        { operator, item, ctx -> singleContainerProvider.invoke(operator, item, ctx)?.let { listOf(it) } ?: emptyList() },
        itemProvider
    )

    override fun isAvailable(
        sub: Subject,
        obj: Object,
        ctx: InteractionContext
    ): AvailableResult {
        val containersCanAccept = containerProvider.invoke(sub, obj, ctx)
            .filter { containerService.canApply(it, itemProvider.invoke(sub, obj, ctx).logic) }

        return if (containersCanAccept.isEmpty()) {
            AvailableResult.No("Предмет нельзя положить в инвентарь")
        } else {
            AvailableResult.Yes
        }
    }
}