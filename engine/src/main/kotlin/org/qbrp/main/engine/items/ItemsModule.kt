package org.qbrp.main.engine.items

import net.minecraft.item.Item.Settings
import net.minecraft.item.ItemStack
import org.koin.core.component.get
import org.koin.dsl.module
import org.qbrp.main.core.Core
import org.qbrp.main.core.assets.prefabs.Prefab
import org.qbrp.main.core.assets.prefabs.PrefabsAPI
import org.qbrp.main.core.game.ComponentRegistryInitializationEvent
import org.qbrp.main.core.mc.player.PlayerObject
import org.qbrp.main.core.mc.player.registration.PlayerRegistrationCallback
import org.qbrp.main.core.mc.registry.items.ItemRegistry
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.engine.items.model.ItemFabric
import org.qbrp.main.engine.items.model.ItemLifecycle
import org.qbrp.main.engine.items.model.ItemStorage
import org.qbrp.main.engine.items.model.ItemTicker
import org.qbrp.main.core.modules.GameModule
import org.qbrp.main.core.storage.StorageAPI
import org.qbrp.main.core.storage.TableAccess
import org.qbrp.main.engine.Engine
import org.qbrp.main.engine.items.components.tooltip.Brief
import org.qbrp.main.engine.items.components.tooltip.Description
import org.qbrp.main.engine.items.components.tooltip.TooltipManager
import org.qbrp.main.engine.synchronization.impl.FuncProvider
import org.qbrp.main.engine.synchronization.impl.SynchronizerChannelSender
import org.qbrp.main.engine.items.model.ServerItemObject
import org.qbrp.main.engine.synchronization.SynchronizationAPI
import org.qbrp.main.engine.synchronization.impl.LocalMessageSender
import org.qbrp.main.engine.synchronization.`interface`.Synchronizer
import org.qbrp.main.engine.synchronization.`interface`.components.ObjectMessageSender

@Autoload
class ItemsModule: GameModule("items") {
    init {
        dependsOn { Core.isApiAvailable<PrefabsAPI>() }
        dependsOn { Engine.isApiAvailable<StorageAPI>() }
        dependsOn { Engine.isApiAvailable<SynchronizationAPI>() }
    }

    companion object {
        val SETTINGS = Settings().maxCount(1)
        val ITEMS_CHANNEL = "items"
        val ITEMS_MESSAGING_CHANNEL = "items_messaging"
    }

    fun giveItemPrefab(key: PrefabEntryKey, player: PlayerObject): Boolean {
        val prefab = get<PrefabsAPI>().loadByKey<Prefab>(key)
        if (prefab != null) {
            val tag = prefab.getTag(key)
            getLocal<ItemFabric>().newInstanceFromPrefab(tag, player, getLocal())
            return true
        }
        return false
    }

    fun copyItemStack(item: ServerItemObject): ItemStack {
        val definition = get<ItemRegistry>().getItem(item.type).get()
        return ItemStack(definition).apply {
            orCreateNbt.putString("id", item.id)
        }
    }

    override fun onLoad() {
        get<PrefabsAPI>().registerPrefabCategory("item")
        val storage = getLocal<ItemStorage<ServerItemObject>>()

        PlayerRegistrationCallback.EVENT.register { player, manager ->
            giveItemPrefab(PrefabEntryKey("item", "test", "default"), player)
        }
        ComponentRegistryInitializationEvent.EVENT.register {
            it.register(TooltipManager::class.java)
            it.register(Brief::class.java)
            it.register(Description::class.java)
        }

        gameAPI.addWorldTickTask(getLocal<ItemTicker>())

        get<SynchronizationAPI>().addProvider(
            FuncProvider(getLocal<Synchronizer>()) { storage.getAll() }
        )
    }

    override fun getKoinModule() = module {
        single<Synchronizer> { SynchronizerChannelSender(ITEMS_CHANNEL) }
        single { ItemStorage<ServerItemObject>() }
        single<TableAccess> { get<StorageAPI>().getTable("items")}
        single { ItemFabric(gameAPI, this@ItemsModule, get()) }
        single { ItemLifecycle(get(), get(), get(), this@ItemsModule, get()) }
        single { ItemTicker(get()) }
        single<ObjectMessageSender> { LocalMessageSender(ITEMS_MESSAGING_CHANNEL) }
    }
}