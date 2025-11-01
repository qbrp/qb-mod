package org.qbrp.main.system

import org.qbrp.main.core.components.ComponentRegistry
import org.qbrp.main.core.fabricmc.ActionBarMessageQueue
import org.qbrp.main.core.fabricmc.ActionBarStatusComponent
import org.qbrp.main.core.fabricmc.CameraOrientationComponent
import org.qbrp.main.core.fabricmc.PlayerModel
import org.qbrp.main.core.fabricmc.PlayerPermissionsComponent
import org.qbrp.main.core.fabricmc.server.CommandHolder
import org.qbrp.main.engine.controls.system.modes.InteractionModeComponent
import org.qbrp.main.engine.physics.player.IsSupportedComponent
import org.qbrp.main.engine.physics.player.MovementEmulation
import org.qbrp.main.engine.physics.player.MovementInputComponent
import org.qbrp.main.engine.physics.player.NewPositionComponent
import org.qbrp.main.engine.physics.player.PlayerMovementAbilities
import org.qbrp.main.engine.physics.player.PlayerPhysicalCharacter
import org.qbrp.main.engine.physics.player.TeleportationComponent
import org.qbrp.main.engine.physics.player.VelocityComponent
import org.qbrp.main.engine.players.attributes.PlayerAttributes
import org.qbrp.main.engine.players.DisplayName
import org.qbrp.main.engine.players.model.PlayerLocation
import org.qbrp.main.engine.spectators.SpectatingMark

fun ComponentRegistry.registerComponents() {
    register { data<CommandHolder>(save = false) }
    register { data<ActionBarStatusComponent>(save = false) }
    register { data<ActionBarMessageQueue>(save = false) }
    register { data<CameraOrientationComponent>(save = false) }
    register { data<PlayerPermissionsComponent>(save = false) }
    register { data<MovementInputComponent>(save = false) }
    register { data<PlayerPhysicalCharacter>(save = false) }
    register { data<PlayerAttributes>() }
    register { data<DisplayName>() }
    register { data<SpectatingMark>(save = false) }
    register { data<PlayerModel>(save = false) }
    register { data<InteractionModeComponent>(save = false) }
    register { data<MovementEmulation>(save = false) }
    register { data<PlayerMovementAbilities>(save = false) }
    register { data<VelocityComponent>(save = false) }
    register { data<TeleportationComponent>(save = false) }
    register { data<NewPositionComponent>(save = false) }
    register { data<IsSupportedComponent>(save = false) }
    register { data<PlayerLocation>(save = false) }
}