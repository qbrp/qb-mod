package org.qbrp.client.network.play

import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayerEntity
import org.qbrp.client.controls.ClientInteractionModeManager
import org.qbrp.client.engine.inventory.ClientInventory
import org.qbrp.client.engine.players.ClientCharactersBank
import org.qbrp.client.engine.players.ClientPlayerFactory
import org.qbrp.client.engine.players.ClientPlayerManager
import org.qbrp.client.engine.players.ClientPlayerStorage
import org.qbrp.client.engine.players.profile.PlayerProfileManager
import org.qbrp.client.network.ClientNetworkModule
import org.qbrp.client.network.transport.registerClientReceiver
import org.qbrp.client.network.transport.sendC2SPacket
import org.qbrp.main.core.fabricmc.player.Username
import org.qbrp.main.core.fabricmc.player.PlayerPermissions
import org.qbrp.main.engine.players.model.PlayerProfile
import org.qbrp.main.core.util.PlayerId
import org.qbrp.main.core.util.math.Vec3
import org.qbrp.main.core.util.math.asVec3
import org.qbrp.main.core.util.math.convertToPhysics
import org.qbrp.main.engine.physics.player.setPosition
import org.qbrp.main.engine.physics.player.setVelocity
import org.qbrp.main.engine.players.characters.entity.CharacterData
import org.qbrp.main.engine.players.model.pos
import org.qbrp.main.engine.world.location.WorldVec3
import org.qbrp.main.engine.world.location.asWorld
import org.qbrp.main.engine.world.location.asWorldVec3
import org.qbrp.main.network.player.CHARACTER_APPLY_ENDPOINT
import org.qbrp.main.network.player.MAIN_PLAYER_POSITION_VELOCITY_ENDPOINT
import org.qbrp.main.network.player.PERMISSION_ENDPOINT
import org.qbrp.main.network.player.PLAYER_ENTITY_ENDPOINT
import org.qbrp.main.network.player.PLAYER_LIST_ENDPOINT
import org.qbrp.main.network.player.UPDATE_FLY_ABILITY_ENDPOINT
import org.qbrp.main.network.player.UpdateFlyAbilityPacket
import org.qbrp.main.network.player.map
import kotlin.collections.forEach

fun Login.requireServerInformation() = serverInformation ?: error("Информация о сервере не была получена")

class ClientPlayerNetworkHandler(
    private val module: ClientNetworkModule,
    private val inventoryManager: ClientInventory,
    private val playerStorage: ClientPlayerStorage,
    private val interactionModeManager: ClientInteractionModeManager,
    private val playerProfileManager: PlayerProfileManager,
    private val playerManager: ClientPlayerManager,
    private val playerFactory: ClientPlayerFactory,
    private val charactersBank: ClientCharactersBank,
    private val client: MinecraftClient
) {

    fun run() {
        PLAYER_LIST_ENDPOINT
            .registerClientReceiver { onPlayerList(playerListDto.map()) }

        PERMISSION_ENDPOINT
            .registerClientReceiver { onPermissions(perms) }

        PLAYER_ENTITY_ENDPOINT
            .registerClientReceiver { onPlayerEntity(PlayerId(id), Username(entityName)) }

        MAIN_PLAYER_POSITION_VELOCITY_ENDPOINT
            .registerClientReceiver { onMainPlayerPositionVelocity(pos.asWorldVec3(), velocity) }

        CHARACTER_APPLY_ENDPOINT
            .registerClientReceiver { onCharacterApply(character) }
    }

    private fun onPlayerList(playerList: List<PlayerProfile>) {
        playerList.forEach { playerProfileManager.addOrUpdateProfile(it) }
    }

    private fun onPermissions(permissions: PlayerPermissions) {
        module.networkState?.permissions = permissions
        interactionModeManager.setAccessibility(permissions)
    }

    private fun onPlayerEntity(id: PlayerId, username: Username) = runInPlayState(module.networkState) {
        if (id == playerManager.clientPlayer?.playerId) return@runInPlayState
        if (playerStorage.getById(id.value) == null) {
            val player = playerFactory.build(client.player!!, id)
            playerStorage.add(player)
        }
    }

    private fun onMainPlayerPositionVelocity(newPos: WorldVec3, newVelocity: WorldVec3) {
        val player = playerManager.clientPlayer ?: return
        val length = newPos.sub(player.pos.asVec3()).length()
        if (length < 0.3f) return
        player.setPosition(newPos)
        player.setVelocity(newVelocity)
    }

    private fun onCharacterApply(character: CharacterData) {
        charactersBank.applyCharacter(character)
        inventoryManager.createCharacterInventories(character)
    }
}

fun sendFlyAbilityPacket(enabled: Boolean) {
    UPDATE_FLY_ABILITY_ENDPOINT
        .sendC2SPacket(UpdateFlyAbilityPacket(enabled))
}