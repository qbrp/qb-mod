package org.qbrp.engine.items

import net.minecraft.item.Item
import org.koin.core.component.get
import org.koin.dsl.module
import org.qbrp.core.assets.prefabs.Prefab
import org.qbrp.core.assets.prefabs.Prefabs
import org.qbrp.core.game.database.ObjectDatabaseService
import org.qbrp.core.mc.player.PlayerObject
import org.qbrp.core.mc.player.registration.PlayerRegistrationCallback
import org.qbrp.core.resources.ServerResources
import org.qbrp.engine.items.model.ItemFabric
import org.qbrp.engine.items.model.ItemLifecycle
import org.qbrp.engine.items.model.ItemStorage
import org.qbrp.engine.items.model.ItemTicker
import org.qbrp.system.modules.GameModule

//@Autoload
class ItemsModule: GameModule("items") {
    fun giveItemPrefab(key: PrefabEntryKey, player: PlayerObject): Boolean {
        val prefab = Prefabs.getByKey<Prefab>(key)
        if (prefab != null) {
            val tag = prefab.getTag(key)
            get<ItemFabric>().newInstanceFromPrefab(tag, player)
            return true
        }
        return false
    }

    override fun onLoad() {
        get<ObjectDatabaseService>().connect()
        PlayerRegistrationCallback.EVENT.register { player, manager ->
            giveItemPrefab(PrefabEntryKey("item", "test", "default"), player)
        }
        gameAPI.addWorldTickTask(get<ItemTicker>())
    }

    override fun getKoinModule() = module {
        single { ItemStorage() }
        single { ObjectDatabaseService(ServerResources.getConfig().databases.nodeUri, "items")
            .apply { connect() }
        }
        single { ItemFabric(gameAPI) }
        single { ItemLifecycle(get(), get(), get()) }
        single { ItemTicker(get()) }
    }

}