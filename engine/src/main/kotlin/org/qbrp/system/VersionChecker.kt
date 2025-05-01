package org.qbrp.system

import klite.NotFoundException
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.server.network.ServerPlayerEntity
import org.qbrp.core.Core
import org.qbrp.system.utils.format.Format.asMiniMessage
import java.util.Timer
import java.util.TimerTask
import kotlin.jvm.optionals.getOrNull

object VersionChecker {
    private val players: MutableList<ServerPlayerEntity> = mutableListOf()
    private val timer = Timer(true)

    val CURRENT_VERSION: Version = Version.fromString(getVersion())

    val COMPATIBLE_CLIENT_VERSION: Version = Version.fromString("Alpha-3.0.0")

    val INCOMPATIBLE_CLIENT_VERSION: Version = Version.fromString("Alpha-2.4.2")

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

    fun getVersionObject() = CURRENT_VERSION

    fun getVersion(): String {
        val version = FabricLoader.getInstance().getModContainer(Core.MOD_ID)
            .map { it.metadata.version.friendlyString }
        return version.getOrNull() ?: throw NotFoundException("Не найдена версия в fabric.mod.json")
    }

    fun check(version: String): CompatibleState {
        return Version.fromString(version).also { println(it) }.isCompatible()
    }

    fun handlePlayer(player: ServerPlayerEntity, version: String) {
        val compatibleState = check(version)
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

    private fun checkReason(version: String, text: String): String {
        return if (version == CURRENT_VERSION.toString()) {
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
    enum class CompatibleState {
        COMPATIBLE,      // Полностью совместимо
        PARTIALLY_COMPATIBLE, // Версия не та, но играть можно
        INCOMPATIBLE     // Полностью несовместимо
    }

    data class Version(
        val releaseStage: String,
        val major: Int,
        val minor: Int,
        val patch: Int,
        val fix: Int,
    ) {
        fun isCompatible(): CompatibleState {
            if (this.releaseStage != COMPATIBLE_CLIENT_VERSION.releaseStage) { return CompatibleState.INCOMPATIBLE }
            if (this.getBuild() == COMPATIBLE_CLIENT_VERSION.getBuild()) { return CompatibleState.COMPATIBLE }
            if (this.getBuild() < INCOMPATIBLE_CLIENT_VERSION.getBuild()) { return CompatibleState.INCOMPATIBLE }
            return CompatibleState.PARTIALLY_COMPATIBLE
        }

        fun getBuild(): Int {
            return (major * 1_000_000) + (minor * 10_000) + (patch * 100) + fix
        }

        override fun toString(): String {
            return "${releaseStage}-${major}.${minor}.${patch}${if (fix != 0) "-$fix" else ""}"
        }

        companion object {
            fun fromString(string: String): Version {
                val parts = string.split("-")
                val releaseStage = parts[0]
                val version = parts[1].split(".")
                val fix = parts.getOrNull(2)?.toIntOrNull() ?: 0
                return Version(releaseStage, version[0].toInt(), version[1].toInt(), version[2].toInt(), fix)
            }
        }
    }

}