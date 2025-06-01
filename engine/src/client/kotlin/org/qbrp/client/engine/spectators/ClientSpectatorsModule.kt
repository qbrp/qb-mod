package org.qbrp.client.engine.spectators

import net.fabricmc.api.EnvType
import org.koin.core.component.get
import org.lwjgl.glfw.GLFW
import org.qbrp.client.ClientCore
import org.qbrp.client.core.keybinds.ClientKeybindsAPI
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.QbModule

@Autoload(env = EnvType.CLIENT)
class ClientSpectatorsModule: QbModule("spectators") {
    init {
        dependsOn { ClientCore.isApiAvailable<ClientKeybindsAPI>() }
    }

    override fun onEnable() {
        get<ClientKeybindsAPI>().registerKeybinding(
            "Появиться",
            GLFW.GLFW_KEY_X,
            "spectators_spawn",
            { }
        )
    }
}