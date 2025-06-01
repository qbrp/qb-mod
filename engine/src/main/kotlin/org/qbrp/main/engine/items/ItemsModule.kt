package org.qbrp.main.engine.items

import net.minecraft.item.ItemStack
import org.koin.core.component.get
import org.koin.dsl.module
import org.qbrp.main.core.Core
import org.qbrp.main.core.assets.prefabs.Prefab
import org.qbrp.main.core.assets.prefabs.Prefabs
import org.qbrp.main.core.assets.prefabs.PrefabsAPI
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
import org.qbrp.main.engine.items.model.ItemObject

@Autoload
class ItemsModule: GameModule("items") {
    init {
        dependsOn { Core.isApiAvailable<PrefabsAPI>() }
    }

    fun giveItemPrefab(key: PrefabEntryKey, player: PlayerObject): Boolean {
        val prefab = get<PrefabsAPI>().getByKey<Prefab>(key)
        if (prefab != null) {
            val tag = prefab.getTag(key)
            getLocal<ItemFabric>().newInstanceFromPrefab(tag, player, getLocal())
            return true
        }
        return false
    }

    fun copyItemStack(item: ItemObject): ItemStack {
        val definition = get<ItemRegistry>().getItem(item.type).get()
        return ItemStack(definition).apply {
            orCreateNbt.putLong("id", item.id)
        }
    }

    override fun onLoad() {
        PlayerRegistrationCallback.EVENT.register { player, manager ->
            giveItemPrefab(PrefabEntryKey("item", "test", "default"), player)
        }
        gameAPI.addTickTask(getLocal<ItemTicker>())
    }

    override fun getKoinModule() = module {
        single { ItemStorage() }
        single { get<StorageAPI>().getTable("items")}
        single { ItemFabric(gameAPI) }
        single { ItemLifecycle(get(), get(), get(), this@ItemsModule) }
        single{ ItemTicker(get()) }
    }

}