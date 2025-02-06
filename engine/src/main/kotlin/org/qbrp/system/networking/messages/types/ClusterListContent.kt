package org.qbrp.system.networking.messages.types

import org.qbrp.system.networking.messages.components.Cluster

class ClusterListContent : ListContent<Cluster>(
    writer = { buf, cluster ->
        cluster.write(buf)
    },
    reader = { buf ->
        val startIndex = buf.readerIndex()
        val cluster = Cluster().convert(buf)
        cluster
    }
)
