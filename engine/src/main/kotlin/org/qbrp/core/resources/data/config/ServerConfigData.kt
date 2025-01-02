package org.qbrp.core.resources.data.config

import com.google.gson.annotations.SerializedName
import org.qbrp.core.resources.ServerResources
import org.qbrp.core.resources.data.Data

data class ServerConfigData(
    val resources: Resources = Resources(),
    val http: HTTP = HTTP()
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
}
