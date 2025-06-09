package org.qbrp.main.engine.players.characters

import com.mongodb.client.model.Filters
import com.mongodb.client.model.Updates
import org.qbrp.main.core.mc.player.Account
import org.qbrp.main.core.mc.player.service.AccountUpdate
import org.qbrp.main.core.mc.player.service.PlayerBehaviour
import org.qbrp.main.core.storage.Table
import org.qbrp.main.engine.characters.model.CharacterData
import org.qbrp.main.engine.players.characters.appearance.Appearance
import org.qbrp.main.engine.players.characters.model.social.SocialKey

class Character(var data: CharacterData): PlayerBehaviour() {
    override val save: Boolean = false
    override fun onEnable() {
        apply()
    }

    override fun onAccountSave(account: Account, db: Table): AccountUpdate {
        return AccountUpdate(
            updates = listOf(
                Updates.set("appliedCharacterName", data.name),
                Updates.set("characters.$[elem].appliedLookName", getComponentOrThrow<Appearance>().look?.name)
            ),
            arrayFilters = listOf(
                Filters.eq("elem.name", account.appliedCharacterName)
            )
        )
    }

    fun getSocialKey(): SocialKey {
        return SocialKey(player.account.uuid, data.id)
    }

    fun apply() {
        val entity = player.entity
        val commands = player.entity.server.commandManager
        try {
            commands.executeWithPrefix(entity.server.commandSource, "scale reset ${player.entityName}")
            commands.executeWithPrefix(entity.server.commandSource, "scale set pehkui:base ${data.scaleFactor} ${player.entityName}")
            commands.executeWithPrefix(entity.server.commandSource, "scale set pehkui:held_item ${1/(data.scaleFactor)} ${player.entityName}")

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