package org.qbrp.main.engine.players.characters.components

import org.qbrp.main.core.components.Activateable
import org.qbrp.main.core.components.ComponentRegistry
import org.qbrp.main.core.components.extensions.get
import org.qbrp.main.core.components.extensions.getOrAdd
import org.qbrp.main.core.components.extensions.require
import org.qbrp.main.core.components.serialization.DTO
import org.qbrp.main.engine.players.characters.CharacterRecord
import org.qbrp.main.engine.players.characters.entity.CharacterData
import org.qbrp.main.engine.players.model.ServerPlayerObject
import org.qbrp.main.engine.players.model.PlayerComponent
import org.qbrp.main.engine.players.model.PlayerComponentBehaviour
import org.qbrp.main.engine.players.model.PlayerComponentInstantiateContext
import org.qbrp.main.engine.text.components.TextSequence
import org.qbrp.main.engine.text.style.Style
import org.qbrp.main.engine.text.style.TextColor
import org.qbrp.main.network.player.Notification
import org.qbrp.main.network.player.sendNotification

interface CharacterBind

interface CharacterBehaviour : PlayerComponentBehaviour, CharacterBind {
    val character: CharacterData
    val bank get() = player.require<CharactersBank>()
    fun upsert(character: CharacterData) {}
}

open class CharacterComponent(
    ctx: CharacterComponentInstantiateContext,
    override var enabled: Boolean = false
) : PlayerComponent(ctx), Activateable, CharacterBehaviour {
    override val character: CharacterData by lazy { ctx.character }
    override val player: ServerPlayerObject by lazy { ctx.player as ServerPlayerObject }
}

class CharacterComponentInstantiateContext(
    player: ServerPlayerObject,
    val character: CharacterData
) : PlayerComponentInstantiateContext(player, player.state)

inline fun <reified D: DTO, reified A: CharacterBehaviour> ComponentRegistry.ComponentEntryBuilder.characterComponentSerialize(
    crossinline factory: (D, CharacterComponentInstantiateContext) -> A
) {
    serializeConstructor<D, CharacterRecord, A> { builder, dto ->
        factory(dto, builder.ctx)
    }
}

fun ServerPlayerObject.createCharactersBank(): CharactersBank {
    return getOrAdd { CharactersBank(componentContext) }
}

fun CharactersBank.applyAccountCharacter() {
    val characters = serverPlayer.account.characters
    val character = serverPlayer.account.appliedCharacter
    if (characters.isNotEmpty()) setCharacterOrBuild(character!!)
}

fun ServerPlayerObject.sendCharacterNotAppliedNotification(scope: String) {
    val notification = Notification.info(
        "Персонаж не применен!",
        TextSequence.Builder()
            .literal("$scope требует применения персонажа. Команда: ")
            .literal("/character <имя>", Style.ofColor(TextColor.AQUA))
            .build()
    )
    sendNotification(notification)
}

fun ServerPlayerObject.sendGenericCharacterNotAppliedNotification() {
    val notification = Notification.info(
        "Персонаж не применен!",
        TextSequence.Builder()
            .literal("Данное действие требует применения персонажа. Команда: ")
            .literal("/character <имя>", Style.ofColor(TextColor.AQUA))
            .build()
    )
    sendNotification(notification)
}

fun ServerPlayerObject.hasCharacter(): Boolean {
    return get<CharactersBank>()?.character != null
}