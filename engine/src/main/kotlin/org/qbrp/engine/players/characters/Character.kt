package org.qbrp.engine.players.characters

import com.fasterxml.jackson.annotation.JsonIgnore
import com.mojang.datafixers.kinds.App
import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.Updates
import org.bson.conversions.Bson
import org.qbrp.core.mc.player.Account
import org.qbrp.core.mc.player.model.PlayerBehaviour
import org.qbrp.engine.Engine
import org.qbrp.engine.characters.model.AppearanceData
import org.qbrp.engine.characters.model.CharacterData
import org.qbrp.engine.chat.ChatAPI
import org.qbrp.engine.players.characters.appearance.Appearance
import org.qbrp.engine.players.characters.model.social.SocialKey

class Character(var data: CharacterData): PlayerBehaviour() {
    override val save: Boolean = false
    override fun onEnable() {
        apply()
    }

    override fun onAccountSave(account: Account, db: MongoDatabase): List<Bson> {
        return listOf(
            Updates.set("appliedCharacterName", data.name),
            Updates.set("characters.$[elem].appliedLookName", getComponentOrThrow<Appearance>().look?.name)
        )
    }

    @JsonIgnore
    fun getSocialKey(): SocialKey {
        return SocialKey(player.account.uuid, data.id)
    }

    fun apply() {
        val entity = player.entity
        try {
            entity.server.commandManager.executeWithPrefix(
                entity.server.commandSource, "scale reset ${player.name}"
            )
            player.entity.server.commandManager.executeWithPrefix(
                entity.server.commandSource, "scale set pehkui:base ${data.scaleFactor} ${player.name}"
            )
            setDescriptionTooltip()
            getComponent<Appearance>()?.apply {
                setModelFromAppearance(data.appearance)
                updateLook(data.appearance.look)
            }
            sendMessage("<gray>Применён персонаж ${data.formattedName}")
        } catch (e: Exception) {
            if (e is NullPointerException) sendMessage("<gray>Персонаж не найден.")
            else sendMessage("<red>Возникла внутренняя ошибка применения персонажа: ${e.message}. Свяжитесь с администратором.")
        }
    }

    fun setDescriptionTooltip() {
        getComponent<Appearance>()?.description = data.appearance.description
    }
}