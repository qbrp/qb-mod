package org.qbrp.client.engine.items

import net.fabricmc.api.EnvType
import net.fabricmc.fabric.api.client.rendering.v1.TooltipComponentCallback
import net.minecraft.client.item.TooltipData
import org.qbrp.client.core.synchronization.ComponentConverter
import org.qbrp.client.core.synchronization.LocalObjectChannel
import org.qbrp.client.core.synchronization.ObjectSynchronizeChannel
import org.qbrp.client.engine.items.components.tooltip.ClientTooltipManager
import org.qbrp.client.engine.items.components.tooltip.ProgressBarTooltip
import org.qbrp.client.engine.items.components.tooltip.mc.TooltipContainer
import org.qbrp.main.core.game.ComponentRegistryInitializationEvent
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.QbModule
import org.qbrp.main.engine.items.ItemsModule
import org.qbrp.main.engine.items.components.tooltip.Description
import org.qbrp.main.engine.items.components.tooltip.TooltipManager
import org.qbrp.main.engine.items.model.ItemStorage

@Autoload(env = EnvType.CLIENT)
class ClientItems() : QbModule("client-items"), ClientItemsAPI {
    override fun getKoinModule() = inner {
        scoped { ItemStorage<ClientItemObject>() }
    }

    override val storage: ItemStorage<ClientItemObject> get() = getLocal()

    override fun onEnable() {
        ObjectSynchronizeChannel<ClientItemObject>(ItemsModule.ITEMS_CHANNEL, storage) { cluster, id ->
            ClientItemObject(cluster.getComponentData("id")!!)
        }
            .addFabric(ComponentConverter(TooltipManager::class, { cluster ->
                ClientTooltipManager(cluster.getComponentData("lines")!!)
            }))
            .addAssociation("TooltipManager", "ClientTooltipManager")
            .addFabric(ComponentConverter(Description::class, { cluster ->
                ProgressBarTooltip()
            }))
            .addAssociation("Description", "ProgressBarTooltip")
            .run()
        LocalObjectChannel<ClientItemObject>(ItemsModule.ITEMS_MESSAGING_CHANNEL, storage)
            .run()
        ComponentRegistryInitializationEvent.EVENT.register {
            it.register(ClientTooltipManager::class.java)
            it.register(ProgressBarTooltip::class.java)
        }

        TooltipComponentCallback.EVENT.register { data: TooltipData? ->
            when (data) {
                is TooltipContainer -> data
                else -> null
            }
        }
    }
}