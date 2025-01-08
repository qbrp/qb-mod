package org.qbrp.engine.client.core.resources

import org.qbrp.core.resources.data.config.ServerConfigData
import org.qbrp.engine.client.core.resources.data.ClientConfigData
import org.qbrp.core.resources.structure.Structure
import java.io.File

class ClientStructure(path: File): Structure(path) {
    val config = open("config.json", ClientConfigData::class.java).data as ClientConfigData
}