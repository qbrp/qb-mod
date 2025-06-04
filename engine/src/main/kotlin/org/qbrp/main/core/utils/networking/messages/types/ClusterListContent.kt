package org.qbrp.main.core.utils.networking.messages.types

import org.qbrp.main.core.utils.networking.messages.components.Cluster

class ClusterListContent : ListContent<Cluster>(
    writer = { buf, cluster ->
        cluster.write(buf)
    },
    reader = { buf ->
        val cluster = Cluster().convert(buf)
        cluster
    }
)
