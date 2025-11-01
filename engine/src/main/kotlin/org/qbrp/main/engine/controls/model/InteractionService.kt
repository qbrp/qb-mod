package org.qbrp.main.engine.controls.model

import org.qbrp.main.core.components.extensions.get
import org.qbrp.main.core.components.extensions.require
import org.qbrp.main.core.util.StringId
import org.qbrp.main.core.fabricmc.player.MinecraftPlayerId
import org.qbrp.main.engine.containers.util.ContainerId
import org.qbrp.main.engine.containers.api.ContainerService
import org.qbrp.main.engine.controls.entity.IconIds
import org.qbrp.main.engine.controls.entity.InteractableTypes
import org.qbrp.main.engine.controls.entity.display.InteractionIcon
import org.qbrp.main.engine.controls.entity.display.InteractableViewData
import org.qbrp.main.engine.controls.entity.interactable.Interactable
import org.qbrp.main.engine.controls.entity.interactable.InteractableType
import org.qbrp.main.engine.controls.model.container.ContainerInteractable
import org.qbrp.main.engine.controls.model.container.ContainerInteractableImpl
import org.qbrp.main.engine.controls.model.player.PlayerInteractable
import org.qbrp.main.engine.controls.model.player.PlayerInteractableImpl
import org.qbrp.main.engine.items.model.ServerItemObject
import org.qbrp.main.engine.players.model.ServerPlayerObject
import org.qbrp.main.engine.controls.model.item.ItemInteractable
import org.qbrp.main.engine.controls.model.item.ItemInteractableImpl
import org.qbrp.main.engine.controls.model.player.CharacterInteractable
import org.qbrp.main.engine.controls.model.player.CharacterInteractableImpl
import org.qbrp.main.engine.controls.model.player.CarryVarianceInteractable
import org.qbrp.main.engine.controls.model.player.CarryVarianceInteractableImpl
import org.qbrp.main.engine.controls.model.player.ItemCarrierInteractable
import org.qbrp.main.engine.controls.model.player.ItemCarrierInteractableImpl
import org.qbrp.main.engine.players.characters.components.CharactersBank
import org.qbrp.main.engine.players.inventory.ItemCarrier
import org.qbrp.main.engine.players.model.location
import org.qbrp.main.engine.text.components.TextComponent
import org.qbrp.main.engine.text.literal
import org.qbrp.main.engine.world.location.Location

class InteractionService(private val containerService: ContainerService) {
    private val playerInteractables: MutableMap<MinecraftPlayerId, PlayerInteractable> = mutableMapOf()

    fun onPlayerJoin(player: ServerPlayerObject) {
        val interactable = PlayerInteractableImpl(
            logic = player,
        )
        playerInteractables[player.uuid] = interactable
    }

    fun onPlayerLeave(playerId: MinecraftPlayerId) {
        playerInteractables.remove(playerId)
    }

    fun getCharacterInteractable(player: ServerPlayerObject): CharacterInteractable? {
        val characterApplied = player.get<CharactersBank>()?.character != null
        if (!characterApplied) return null
        return CharacterInteractableImpl(
            player,
            carrier = getItemCarrierInteractable(
                player.require<ItemCarrier>(),
                InteractableTypes.HAND,
                literal("Рука"),
                InteractionIcon.Asset(IconIds.HAND)
            )
        )
    }

    fun getPlayerInteractable(player: ServerPlayerObject): PlayerInteractable {
        return playerInteractables[player.uuid]!!
    }

    fun getContainerInteractable(container: ContainerId, entity: Interactable): ContainerInteractable {
        return ContainerInteractableImpl(
            container,
            entity,
            containerService.getName(container).value,
            containerService.getAnchoredEntity(container)?.id?.let { StringId(it) }
        )
    }

    fun getItemCarrierInteractable(
        itemCarrier: ItemCarrier,
        type: InteractableType<ItemCarrierInteractable>,
        name: TextComponent,
        icon: InteractionIcon = getPlayerInteractable(itemCarrier.owner).interactableDisplay.icon
    ): ItemCarrierInteractable {
        val display = InteractableViewData.of(name, icon)
        return ItemCarrierInteractableImpl(
            itemCarrier,
            type,
            display,
            getCarryVariancesInteractables(display, itemCarrier, itemCarrier.owner.location)
        )
    }

    fun getCarryVariancesInteractables(parentDisplay: InteractableViewData, carrierLogic: ItemCarrier, location: Location): List<CarryVarianceInteractable> {
        return carrierLogic.variances.map {
            val name = literal(containerService.getName(it.id).value)
            CarryVarianceInteractableImpl(
                location,
                it,
                InteractableViewData.of(name, parentDisplay.icon)
            )
        }
    }

    fun getItemInteractable(itemObject: ServerItemObject): ItemInteractable {
        return ItemInteractableImpl(itemObject, containerService, this)
    }
}