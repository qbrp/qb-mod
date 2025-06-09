package org.qbrp.main.engine.players.inventory

import net.minecraft.util.ActionResult
import org.koin.core.component.get
import org.qbrp.main.core.Core
import org.qbrp.main.core.game.prefabs.PrefabField
import org.qbrp.main.core.keybinds.ServerKeybindCallback
import org.qbrp.main.core.keybinds.ServerKeybindsAPI
import org.qbrp.main.core.mc.player.PlayersAPI
import org.qbrp.main.core.mc.player.PlayersUtil
import org.qbrp.main.core.modules.QbModule
import org.qbrp.main.core.utils.Deps

//@Autoload(LoadPriority.LOWEST)
class PlayerInventoryModule: QbModule("player-inventory") {
    init {
        dependsOn { Core.isApiAvailable<ServerKeybindsAPI>() }
        dependsOn { Core.isApiAvailable<PlayersAPI>() }
    }

    override fun onEnable() {
        get<ServerKeybindsAPI>().registerKeybindReceiver("open_inventory")
        ServerKeybindCallback.getOrCreateEvent("open_inventory").register { player ->
            PlayersUtil.getPlayerSession(player).getComponent<PlayerInventory>()
            ActionResult.SUCCESS
        }
        Deps.PLAYER_PREFAB.components += PrefabField { PlayerInventory() }
    }
}