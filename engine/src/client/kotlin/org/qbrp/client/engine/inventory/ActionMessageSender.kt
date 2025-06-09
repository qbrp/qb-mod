package org.qbrp.client.engine.inventory

interface ActionMessageSender {
    fun sendTakeItemMessage(slot: Int, containerId: String, objectId: String)
    fun sendPutItemMessage(slot: Int, containerId: String, objectId: String)
    fun sendSwapItemMessage(slot: Int, containerId: String, objectId: String)
}