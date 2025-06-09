package org.qbrp.main.engine.items

import net.minecraft.item.Item.Settings
import org.koin.core.component.get
import org.koin.dsl.module
import org.qbrp.main.core.Core
import org.qbrp.main.core.assets.prefabs.Prefab
import org.qbrp.main.core.assets.prefabs.PrefabsAPI
import org.qbrp.main.core.game.ComponentRegistryInitializationEvent
import org.qbrp.main.core.game.saving.ServerStopSaver
import org.qbrp.main.core.game.saving.TimerSaver
import org.qbrp.main.core.mc.player.ServerPlayerObject
import org.qbrp.main.core.mc.player.registration.PlayerAuthEvent
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.engine.items.model.ItemFabric
import org.qbrp.main.engine.items.model.ItemLifecycle
import org.qbrp.main.engine.items.model.ItemStorage
import org.qbrp.main.engine.items.model.ItemTicker
import org.qbrp.main.core.modules.GameModule
import org.qbrp.main.core.storage.StorageAPI
import org.qbrp.main.core.storage.TableAccess
import org.qbrp.main.engine.Engine
import org.qbrp.main.engine.inventory.AbstractContainer
import org.qbrp.main.engine.items.components.model.ItemModel
import org.qbrp.main.engine.items.components.physics.Material
import org.qbrp.main.engine.items.components.physics.Physics
import org.qbrp.main.engine.items.components.tooltip.impl.Brief
import org.qbrp.main.engine.items.components.tooltip.impl.Description
import org.qbrp.main.engine.items.components.tooltip.impl.ItemDisplay
import org.qbrp.main.engine.items.components.tooltip.impl.Name
import org.qbrp.main.engine.items.model.LeavePlayerInventorySaver
import org.qbrp.main.engine.items.model.ItemRepository
import org.qbrp.main.core.synchronization.impl.FuncProvider
import org.qbrp.main.core.synchronization.impl.SynchronizerChannelSender
import org.qbrp.main.engine.items.model.ServerItemObject
import org.qbrp.main.core.synchronization.SynchronizationAPI
import org.qbrp.main.core.synchronization.impl.LocalMessageSender
import org.qbrp.main.core.synchronization.Synchronizer
import org.qbrp.main.core.synchronization.channels.ObjectMessagingChannel
import org.qbrp.main.core.synchronization.components.MessagingChannelSender
import org.qbrp.main.engine.inventory.InventorySynchronizer

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
        val TICK_RATE = 10
    }

    fun giveItemPrefab(key: PrefabEntryKey, player: ServerPlayerObject): Boolean {
        val prefab = get<PrefabsAPI>().loadByKey<Prefab>(key)
        if (prefab != null) {
            val tag = prefab.getTag(key)
            getLocal<ItemFabric>().newInstanceFromPrefab(tag, this)
                .apply { give(player) }
            return true
        }
        return false
    }

    fun createItem(key: PrefabEntryKey): ServerItemObject {
        val prefab = get<PrefabsAPI>().loadByKey<Prefab>(key)!!.getTag(key)
        return getLocal<ItemFabric>().newInstanceFromPrefab(prefab, this)
    }

    override fun onLoad() {
        get<PrefabsAPI>().registerPrefabCategory("item")
        val storage = get<ItemStorage<ServerItemObject>>()
        val repo = get<ItemRepository>()

        TimerSaver<ServerItemObject>("Items", 4000L * 40L)
            .run(storage, repo)
        ServerStopSaver<ServerItemObject>()
            .run(storage, repo)
        LeavePlayerInventorySaver()
            .run(storage, repo)

        ObjectMessagingChannel<ServerItemObject>(ITEMS_MESSAGING_CHANNEL, get<ItemStorage<ServerItemObject>>())
            .run()

        PlayerAuthEvent.EVENT.register { player, manager ->
            giveItemPrefab(PrefabEntryKey("item", "test", "default"), player)
        }
        ComponentRegistryInitializationEvent.EVENT.register {
            it.register(ItemDisplay::class.java)
            it.register(Brief::class.java)
            it.register(Description::class.java)
            it.register(Name::class.java)
            it.register(ItemModel::class.java)
            it.register(Physics::class.java)
            it.register(Material::class.java)
            it.register(AbstractContainer::class.java)
            it.register(InventorySynchronizer::class.java)
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
        single { ItemRepository(this@ItemsModule, get(), get(), get()) }
        single { ItemFabric(gameAPI) }
        single { ItemLifecycle(get(), get()) }
        single { ItemTicker(get()) }
        single<MessagingChannelSender> { LocalMessageSender(ITEMS_MESSAGING_CHANNEL) }
        single { this } //ТОЛЬКО ДЛЯ ТЕСТОВ
    }
}