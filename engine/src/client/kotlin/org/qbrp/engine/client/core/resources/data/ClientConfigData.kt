package org.qbrp.engine.client.core.resources.data

import com.google.gson.annotations.SerializedName
import org.qbrp.core.resources.data.Data
import java.nio.file.Path
import java.nio.file.Paths

data class ClientConfigData(val resources: Resources = Resources()) : Data() {
    override fun toFile(): String {
        return gson.toJson(this)
    }

    class Resources {
        val host: String = "0.0.0.0"
        val port: Int = 25008
        val request: String = "resourcepack.zip"

        val downloadUrl: String
            get() = "http://$host:$port/$request"
    }

}