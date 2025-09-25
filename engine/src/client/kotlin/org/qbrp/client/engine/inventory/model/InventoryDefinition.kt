package org.qbrp.client.engine.inventory.model

import org.qbrp.main.engine.containers.v2.components.ContainerDisplayName
import org.qbrp.main.engine.inventory.entity.InventoryParams

class InventoryDefinition(val container: ClientContainerState,
                          val params: InventoryParams = InventoryParams.DEFAULT)