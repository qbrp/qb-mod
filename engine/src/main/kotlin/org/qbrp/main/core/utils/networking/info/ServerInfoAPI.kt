package org.qbrp.main.core.utils.networking.info

import org.qbrp.main.core.utils.networking.messages.components.ClusterBuilder
import org.qbrp.main.core.utils.networking.messages.components.readonly.ClusterViewer

interface ServerInfoAPI {
    val COMPOSER: ClusterBuilder
    fun broadcast()
}