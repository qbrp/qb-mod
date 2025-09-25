package org.qbrp.main.engine.controls.entity.display

class InteractionContextMenuViewData(val objectInteractions: List<Entry>) {
    fun isNotEmpty() = objectInteractions.isNotEmpty()
    val count get() = objectInteractions.count()
    constructor(map: Map<InteractableViewData, List<InteractionViewNode>>): this(
        map.map { (k, v) -> Entry(k, v) }
    )
    class Entry(val obj: InteractableViewData, val nodes: List<InteractionViewNode>)
}