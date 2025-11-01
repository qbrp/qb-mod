package org.qbrp.main.network.transport

import org.qbrp.main.core.Core

enum class NetworkPlatform {
    SERVER, CLIENT
}

fun getPlatform() = if (Core.isClient) NetworkPlatform.CLIENT else NetworkPlatform.SERVER