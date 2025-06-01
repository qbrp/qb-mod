package org.qbrp.client.core.networking.info

import org.qbrp.main.core.utils.networking.messages.components.readonly.ClusterViewer

interface ServerInfoReader {
    val VIEWER: ClusterViewer
}