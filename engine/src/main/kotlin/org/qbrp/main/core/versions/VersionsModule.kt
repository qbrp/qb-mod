package org.qbrp.main.core.versions

import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents
import net.minecraft.server.network.ServerPlayerEntity
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.QbModule
import org.qbrp.main.core.utils.networking.messages.Messages.HANDLE_VERSION
import org.qbrp.main.core.utils.networking.messages.types.StringContent
import org.qbrp.main.core.utils.networking.messaging.ServerReceiver
import org.qbrp.main.core.utils.networking.messaging.ServerReceiverContext
import org.qbrp.main.core.utils.format.Format.asMiniMessage
import org.qbrp.main.core.versions.VersionsUtil.getVersion
import java.util.Timer
import java.util.TimerTask

@Autoload
class VersionsModule: QbModule("versions") {
    private val players: MutableList<ServerPlayerEntity> = mutableListOf()
    private val timer = Timer(true)

    companion object {
        val CURRENT_VERSION: Version = getVersion()
        val COMPATIBLE_CLIENT_VERSION: Version = getVersion()
        val INCOMPATIBLE_CLIENT_VERSION: Version = getVersion()
    }

    override fun onEnable() {
        ServerPlayConnectionEvents.JOIN.register { handler, sender, server ->
            addPlayerTask(handler.player)
        }
        ServerReceiver<ServerReceiverContext>(HANDLE_VERSION, StringContent::class, { message, context, receiver ->
            handlePlayer(context.player, Version.fromString(message.getContent()))
            true
        }).register()
    }

    fun addPlayerTask(player: ServerPlayerEntity) {
        if (players.contains(player)) { players.remove(player) }
        players.add(player)
        timer.schedule(object : TimerTask() {
            override fun run() {
                if (players.contains(player)) {
                    kickIfInList(player)
                }
            }
        }, 15000)
    }

    fun isCompatible(version: Version): CompatibleState {
        if (version.releaseStage != COMPATIBLE_CLIENT_VERSION.releaseStage) { return CompatibleState.INCOMPATIBLE }
        if (version.getBuild() == COMPATIBLE_CLIENT_VERSION.getBuild()) { return CompatibleState.COMPATIBLE }
        if (version.getBuild() < INCOMPATIBLE_CLIENT_VERSION.getBuild()) { return CompatibleState.INCOMPATIBLE }
        return CompatibleState.PARTIALLY_COMPATIBLE
    }

    fun handlePlayer(player: ServerPlayerEntity, version: Version) {
        val compatibleState = isCompatible(version)
        when (compatibleState) {
            CompatibleState.PARTIALLY_COMPATIBLE -> {
                player.sendMessage(
                    ("<yellow>Версия вашего мода engine ($version) не совпадает с версией мода сервера (${CURRENT_VERSION}). Обновите мод." +
                            "<newline>Некоторые функции могут неправильно работать, вызывать баги и вылеты игры.").asMiniMessage()
                )
            }
            CompatibleState.COMPATIBLE -> { }
            else -> player.networkHandler.disconnect(
                checkReason(version, "<red>Версия вашего мода engine ($version) несовместима с версией мода сервера." +
                        "<newline>Обновите мод до минимально допустимой ($INCOMPATIBLE_CLIENT_VERSION) или последней ($CURRENT_VERSION).").asMiniMessage()
            )
        }
        players.remove(player)
    }

    private fun checkReason(version: Version, text: String): String {
        return if (version == CURRENT_VERSION) {
            "<red>Сервер загружается. Попробуйте зайти позже.<newline>Если эта ошибка наблюдается после 2-3 минут, свяжитесь с администратором."
        } else {
            text
        }
    }

    private fun kickIfInList(player: ServerPlayerEntity) {
        player.networkHandler.disconnect(
            ("<red>Версия вашего мода engine несовместима с версией мода сервера." +
                    "<newline>Обновите мод до минимально допустимой ($INCOMPATIBLE_CLIENT_VERSION) или последней ($CURRENT_VERSION).").asMiniMessage()
        )
        players.remove(player)
    }

}