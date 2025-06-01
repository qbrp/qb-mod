package org.qbrp.deprecated.resources

import org.qbrp.deprecated.resources.data.StringData
import org.qbrp.deprecated.resources.data.config.ConfigInitializationCallback
import org.qbrp.deprecated.resources.data.config.ServerConfigData
import org.qbrp.deprecated.resources.structure.Structure
import org.qbrp.deprecated.resources.units.TextUnit
import java.io.File

class ServerStructure: Structure(File("qbrp")) {
    init {
        initFile()
    }
    var config = openConfig()

    val records = addBranch("records")
    val youtubeToken = open("token.youtube-token", StringData::class.java)

    private fun openConfig() = (open("config.yml", ServerConfigData::class.java).data as ServerConfigData)
        .also { ConfigInitializationCallback.Companion.EVENT.invoker().onConfigUpdated(it) }
    fun reloadConfig() {
        config = openConfig()
    }
}
