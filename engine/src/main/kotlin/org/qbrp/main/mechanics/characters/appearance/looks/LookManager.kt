package org.qbrp.main.mechanics.characters.appearance.looks

import kotlinx.serialization.Serializable
import org.qbrp.main.engine.characters.model.AppearanceData.Look
import org.qbrp.main.mechanics.characters.model.CharacterData
import org.qbrp.main.engine.players.characters.components.CharacterComponent
import org.qbrp.main.engine.players.characters.components.CharacterComponentInstantiateContext
import org.qbrp.main.engine.players.characters.components.CharactersBank
import org.qbrp.main.core.components.extensions.require
import org.qbrp.main.core.components.serialization.DTO

class LookManager(
    ctx: CharacterComponentInstantiateContext,
    private var looks: List<Look> = ctx.character.appearance.looks,
    var description: String = ctx.character.appearance.description,
    var look: Look = ctx.character.appearance.look
): CharacterComponent(ctx) {
    val defaultLook: Look get() = looks[0]

    @Serializable data class Data(val look: Look): DTO
    override val dto: DTO? get() = Data(look)

    override fun onEnable() {
        upsert(look)
    }

    fun upsert(look: Look) = injectContext {
        require<Skin>().apply(getSkinOrDefault())
    }

    fun updateLook(name: String): Boolean {
        looks.find { it.name == name }?.let {
            look = it
            upsert(it)
            return true
        }
        return false
    }

    fun getFormattedName(): String {
        return look.getColoredName(require<CharactersBank>().character!!.data)
    }

    fun getDescriptionOrDefault(): String {
        return description + (look.description?.let { "<newline>$it" } ?: "")
    }

    fun getSkinOrDefault(): String {
        return look.skinUrl ?: defaultLook.skinUrl!!
    }

    override fun upsert(character: CharacterData) {
        looks = character.appearance.looks
        description = character.appearance.description
    }
}