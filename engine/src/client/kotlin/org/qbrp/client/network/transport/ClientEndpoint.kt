package org.qbrp.client.network.transport

import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.qbrp.main.network.transport.Endpoint
import org.qbrp.main.network.transport.Packet

object ClientPlatformDependencies : KoinComponent {
    internal val transportProgram: ClientPacketTransportProgram by inject()

//    fun fetchClientTransportProgram(): ClientPacketTransportProgram {
//        return getKoin().fetchInPlatform(transportProgram, NetworkPlatform.CLIENT)
//            .also { transportProgram = it }
//    }

    fun fetchClientTransportProgram(): ClientPacketTransportProgram {
        return transportProgram
    }
}

fun <P : Packet> Endpoint<P>.registerClientReceiver(handler: ClientPacketHandler<P>) {
    ClientPlatformDependencies.fetchClientTransportProgram().registerClientReceiver(channel, handler)
}