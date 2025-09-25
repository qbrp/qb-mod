package core.networking

import org.qbrp.main.core.networking.NetworkContext
import org.qbrp.main.core.networking.ServerNetworkContext

class GenericPredictedPacketListenerActivator: PredictedPacketListenerActivator<NetworkContext>() {
    override fun newContext(): NetworkContext {
        return object : NetworkContext {}
    }
}