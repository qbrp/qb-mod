package org.qbrp.engine.client.core.resources

import org.qbrp.core.resources.data.Data

data class ClientConfigData(
    val resources: Resources = Resources(),
    val account: Account = Account()
) : Data()
{
    override fun toFile(): String {
        return gson.toJson(this)
    }

    class Resources {
        val host: String = "imperialhell.org:25002"
        val port: Int = 25008
        val request: String = "resourcepack.zip"

        val downloadUrl: String
            get() = "http://$host:$port/$request"
    }

    class Account {
        var code: String = "NONE"
    }

}