package org.qbrp.main.mechanics.characters.physical

import org.koin.core.component.get
import org.qbrp.main.core.components.ComponentRegistryInitializationEvent
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.Priorities
import org.qbrp.main.core.modules.EngineModule
import org.qbrp.main.engine.players.characters.components.characterComponent

@Autoload(Priorities.CHARACTERS)
class CharactersPhysicalModule: EngineModule("characters-physical") {
    override fun onEnable() {
        ComponentRegistryInitializationEvent.EVENT.register { registry ->
            registry.register {
                componentClass(Height::class)
                characterComponent { Height(it, get()) }
            }
            registry.register {
                data<CharacterPhysicalParameters>()
                characterComponent { ctx -> CharacterPhysicalParameters(ctx.character) }
            }
        }
    }
}