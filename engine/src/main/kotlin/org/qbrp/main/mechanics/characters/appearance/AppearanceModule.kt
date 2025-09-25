package org.qbrp.main.mechanics.characters.appearance

import org.koin.core.component.get
import org.koin.core.module.Module
import org.qbrp.main.core.components.ComponentRegistryInitializationEvent
import org.qbrp.main.core.mc.commands.CommandsAPI
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.EngineModule
import org.qbrp.main.engine.players.characters.components.characterComponent
import org.qbrp.main.engine.players.characters.components.characterComponentSerialize
import org.qbrp.main.mechanics.characters.appearance.looks.ApplyLookCommand
import org.qbrp.main.mechanics.characters.appearance.looks.LookManager
import org.qbrp.main.mechanics.characters.appearance.looks.Skin

@Autoload
class AppearanceModule: EngineModule("character-appearance") {
    override fun onEnable() {
        ComponentRegistryInitializationEvent.EVENT.register {
            it.register {
                componentClass(LookManager::class)
                characterComponent {
                    LookManager(it)
                }
                characterComponentSerialize<LookManager.Data, LookManager> { dto, ctx ->
                    LookManager(ctx = ctx, look = dto.look)
                }
            }
            it.register {
                componentClass(Skin::class)
                characterComponent(::Skin)
            }
        }
        get<CommandsAPI>().add(getLocal<ApplyLookCommand>())
    }

    override fun getKoinModule(): Module = inner {
        scoped { ApplyLookCommand() }
    }
}