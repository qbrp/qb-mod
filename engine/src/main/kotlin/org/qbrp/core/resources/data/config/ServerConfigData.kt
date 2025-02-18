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
) : Data() {

    override fun toFile(): String = gson.toJson(this).also { ServerResources.getLogger().log(this.toString()) }

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
        val groups: String = "groups")

    data class Music(val priorities: List<String> = listOf())

    data class Chat(
        val chatGroups: List<ChatGroup> = listOf(),
        val joinMessage: String = "+ {player}",
        val leaveMessage: String = "- {player}"
    )

    data class Time(
        val periods: List<Period> = listOf(),
        val formatDo: String = "&6&l{time}",
        val doFrequency: Int = 2,
        val rpTimeOffset: Int = 0
    ) {
    }
}
