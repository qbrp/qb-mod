package org.qbrp.main.engine.players.characters.components

import org.koin.core.context.GlobalContext
import org.qbrp.main.core.CoreContext
import org.qbrp.main.core.components.AnyComponent
import org.qbrp.main.core.components.ComponentRegistry
import org.qbrp.main.core.components.extensions.get
import org.qbrp.main.core.components.extensions.getOrAdd
import org.qbrp.main.core.components.extensions.toDTO
import org.qbrp.main.core.components.serialization.DTO
import org.qbrp.main.core.notifications.entity.Notification
import org.qbrp.main.engine.Colors
import org.qbrp.main.engine.players.characters.entity.CharacterComponentBuilder
import org.qbrp.main.engine.players.characters.entity.CharacterFactory
import org.qbrp.main.engine.players.characters.entity.CharacterProfile
import org.qbrp.main.engine.players.characters.entity.CharacterProfileDTO
import org.qbrp.main.engine.players.model.ServerPlayerObject
import org.qbrp.main.engine.text.components.TextSequence
import org.qbrp.main.engine.text.style.Style
import org.qbrp.main.engine.text.style.TextColor

inline fun <reified T: AnyComponent> ComponentRegistry.ComponentEntryBuilder.characterComponent(crossinline factory: (CharacterComponentInstantiateContext) -> T) {
    GlobalContext.get().get<CharacterFactory>().builders.add { ctx, profile ->
        profile.getOrAdd(GlobalContext.get().get<ComponentRegistry>().getComponentName(T::class), false) { factory(ctx) }
    }
}

fun ComponentRegistry.ComponentEntryBuilder.onBuildCharacter(factory: CharacterComponentBuilder) {
    GlobalContext.get().get<CharacterFactory>().builders.add(factory)
}

inline fun <reified D: DTO, reified A: CharacterBehaviour> ComponentRegistry.ComponentEntryBuilder.characterComponentSerialize(
    crossinline factory: (D, CharacterComponentInstantiateContext) -> A
) {
    serializeConstructor<D, CharacterProfile, A> { builder, dto ->
        factory(dto, builder.ctx)
    }
}

fun MutableList<CharacterProfile>.toDtoAll(): List<CharacterProfileDTO> {
    return map { it.profileToDTO() }
}

fun CharacterProfile.profileToDTO(): CharacterProfileDTO {
    return CharacterProfileDTO(id, data, state.toDTO())
}

context(core: CoreContext)
fun ServerPlayerObject.sendCharacterNotAppliedNotification(scope: String) {
    val notification = Notification.info(
        "Персонаж не применен!",
        TextSequence.Builder()
            .literal("$scope требует применения персонажа. Команда: ")
            .literal("/character <имя>", Style.ofColor(TextColor.AQUA))
            .build()
    )
    core.notificationManager.send(this, notification)
}

context(core: CoreContext)
fun ServerPlayerObject.sendGenericCharacterNotAppliedNotification() {
    val notification = Notification.info(
        "Персонаж не применен!",
        TextSequence.Builder()
            .literal("Данное действие требует применения персонажа. Команда: ")
            .literal("/character <имя>", Style.ofColor(TextColor.AQUA))
            .build()
    )
    core.notificationManager.send(this, notification)
}

fun ServerPlayerObject.hasCharacter(): Boolean {
    return get<CharactersBank>()?.character != null
}