package org.qbrp.core.resources.data.config

import com.google.gson.annotations.SerializedName
import org.qbrp.core.resources.ServerResources
import org.qbrp.core.resources.data.Data
import org.qbrp.engine.chat.core.system.ChatGroup
import org.qbrp.engine.time.Period

data class ServerConfigData(
    val resources: Resources = Resources(),
    val http: HTTP = HTTP(),
    val databases: Databases = Databases(),
    val music: Music = Music(),
    val chat: Chat = Chat(),
    val time: Time = Time(),
    val players: Players = Players()
) : Data() {

    val disabledModules: List<String> = listOf()

    override fun toFile(): String = gson.toJson(this).also { ServerResources.getLogger().log(this.toString()) }

    data class Chat(
        val chatGroups: List<ChatGroup> = listOf(),
        val joinMessage: String = "+ {player}",
        val leaveMessage: String = "- {player}",
        val commands: Commands = Commands(),
        val volume: Volume = Volume(),
    ) {
        data class Commands(val formatMe: String = "{playerDisplayName} &e* &f{text}",
                          val formatDo: String = "{playerDisplayName} &d( &f{text} &d)",
                          val formatGdo: String = "{playerDisplayName} &d( &f{text} &d)",
                          val formatRoll: String = "{playerDisplayName} &b* &f{text} &b- &a{roll}%")

        data class Volume(val volumePrefixes: Map<String, Int> = mapOf(),
                          val blockVolumes: Map<String, Double> = mapOf(),
                          val tagVolumes: Map<String, Double> = mapOf(),
                          val formatVolume: Map<Int, String> = mapOf(
                              5 to "&7",
                              15 to "&f",
                              25 to "&c",
                              45 to "&c&l",
                              80 to "&4&l",
                          ),
                          val formatMinVolume: String = "&7&o",
                          val defaultVolumeBlockModifier: Double = 5.0,
                          val defaultVolume: Int = 25,
                          val distortionLevel: Int = 15,
                          val maxDistortion: Int = 70,
                          val artifacts: List<String> = listOf("#", "-", "...", "*", "~", "^"))
    }

    data class Players(
        val defaultSurvivalSpeed: Int = 1,
        val defaultCreativeSpeed: Int = 1,
        val defaultSpectatorSpeed: Int = 1
    )

    data class HTTP(
        val port: Int = 25008,
        @SerializedName("resource_pack_path")
        val resourcePack: String = "qbrp/resources/resourcepack.zip"
    )

    data class Resources(
        @SerializedName("pack_mc_meta")
        val packMcMeta: Pack = Pack()
    ) {

        data class Pack(
            val description: String = "",

            @SerializedName("pack_format")
            val packFormat: Int = 15 // Используем правильное имя поля, чтобы оно соответствовало JSON
        )
    }

    data class Databases(
        @SerializedName("node_uri")
        val nodeUri: String = org.qbrp.system.secrets.Databases.MAIN,
        val items: String = "items",
        val blocks: String = "serverBlocks",
        val music: String = "music",
        val regions: String = "regions",
        val groups: String = "chatGroups")

    data class Music(val priorities: List<String> = listOf())

    data class Time(
        val periods: List<Period> = listOf(),
        val formatDo: String = "&6&l{time}",
        val doFrequency: Int = 2,
        val rpTimeOffset: Int = 0
    ) {
    }
}
