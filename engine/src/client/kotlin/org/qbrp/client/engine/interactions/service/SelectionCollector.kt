package org.qbrp.client.engine.interactions.service

import org.qbrp.client.engine.items.ClientItemObject
import org.qbrp.main.engine.containers.util.ContainerId

interface SelectionCollector {
    fun getSelectedInventory(): ContainerId?
    fun getSelectedItem(): ClientItemObject?
}