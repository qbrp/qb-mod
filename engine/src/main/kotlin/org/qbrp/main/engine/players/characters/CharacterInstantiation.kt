package org.qbrp.main.engine.players.characters

import kotlinx.serialization.Serializable
import org.koin.core.context.GlobalContext
import org.qbrp.main.core.components.ComponentManager
import org.qbrp.main.core.components.extensions.getOrAdd
import org.qbrp.main.core.components.extensions.toDTO
import org.qbrp.main.core.components.state.ComponentState
import org.qbrp.main.core.components.state.StateJsonField
import org.qbrp.main.engine.containers.api.ContainerService
import org.qbrp.main.engine.players.characters.components.CharacterBehaviour
import org.qbrp.main.engine.players.characters.components.CharacterComponentInstantiateContext
import org.qbrp.main.engine.players.inventory.CharacterInventory
import org.qbrp.main.engine.players.inventory.CharacterInventoryContainers
import org.qbrp.main.engine.players.inventory.Hand
import org.qbrp.main.engine.players.inventory.createEquipmentContainer
import org.qbrp.main.engine.players.inventory.createHandContainer
import org.qbrp.main.engine.players.model.ServerPlayerObject
import org.qbrp.main.engine.players.characters.components.LookManager
import org.qbrp.main.engine.players.characters.components.Skin
import org.qbrp.main.engine.players.characters.entity.CharacterData
import org.qbrp.main.engine.players.characters.components.Height
import org.qbrp.main.engine.players.customName

fun loadCharacterComponents(
    ctx: CharacterComponentInstantiateContext,
    containerService: ContainerService = GlobalContext.get().get()
) {
    val player = ctx.player
    val character = ctx.character
    val containers = containerService.let {
        CharacterInventoryContainers(
            it.createHandContainer(character, Hand.Side.RIGHT),
            it.createHandContainer(character, Hand.Side.LEFT),
            it.createEquipmentContainer(character)
        )
    }
    player.getOrAdd { CharacterInventory(ctx, containers, containerService) }
    player.customName = character.formattedName
}

fun createCharacterProfile(
    ctx: CharacterComponentInstantiateContext,
): CharacterRecord {
    val player = ctx.player as ServerPlayerObject
    val state = ComponentState<ServerPlayerObject>()
    state.parent = player
    val profile = CharacterRecord(player, ctx.character.id, ctx.character, state)

    profile.getOrAdd { LookManager(ctx) }
    profile.getOrAdd { Skin(ctx) }
    profile.getOrAdd { Height(ctx) }

    return profile
}

fun upsertCharacter(
    character: CharacterRecord,
    ctx: CharacterComponentInstantiateContext
) {
    character.forEach { if (it is CharacterBehaviour) it.upsert(character.data) }
}

class CharacterRecord(
    val player: ServerPlayerObject,
    val id: Int,
    val data: CharacterData,
    val state: ComponentManager
): ComponentManager by state {
    val ctx get() = CharacterComponentInstantiateContext(player, data)

    override fun equals(other: Any?): Boolean {
        return other is CharacterRecord && id == other.id
    }

    override fun hashCode(): Int {
        return id
    }
}

@Serializable
class CharacterRecordDto(val id: Int, val data: CharacterData, val state: StateJsonField) {
    override fun equals(other: Any?): Boolean {
        return other is CharacterRecordDto && id == other.id
    }

    override fun hashCode(): Int {
        return id
    }
}

fun MutableList<CharacterRecord>.toDtoAll(): List<CharacterRecordDto> {
    return map { it.profileToDTO() }
}

fun CharacterRecord.profileToDTO(): CharacterRecordDto {
    return CharacterRecordDto(id, data, state.toDTO())
}