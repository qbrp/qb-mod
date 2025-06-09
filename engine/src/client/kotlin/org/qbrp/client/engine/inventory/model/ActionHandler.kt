package org.qbrp.client.engine.inventory.model

interface ActionHandler {
    fun takeItem(slot: Int, containerId: String)
    fun putItem(slot: Int, containerId: String)
    fun swapItem(slot: Int, containerId: String)

}