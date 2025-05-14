package org.qbrp.engine.client.system

import org.qbrp.engine.client.EngineClient
import org.qbrp.system.modules.QbModule

abstract class ClientModule(name: String): QbModule(name) {
    override fun ifEnabled(method: () -> Unit) {
        if (EngineClient.moduleManager.isModuleEnabled(getName())) method()
    }
}