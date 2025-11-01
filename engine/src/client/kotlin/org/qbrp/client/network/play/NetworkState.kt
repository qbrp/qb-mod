package org.qbrp.client.network.play

import org.qbrp.main.core.fabricmc.player.PlayerPermissions
import org.qbrp.main.network.player.ConnectionSession
import org.qbrp.main.network.player.ServerInformation

sealed interface NetworkState {
    val serverInformation: ServerInformation?
    val session: ConnectionSession?
    var permissions: PlayerPermissions?
}

class Login(
    override var serverInformation: ServerInformation? = null,
    override var session: ConnectionSession? = null,
    override var permissions: PlayerPermissions? = null
) : NetworkState

class Play(
    override val serverInformation: ServerInformation,
    override val session: ConnectionSession,
    override var permissions: PlayerPermissions?
) : NetworkState

fun runInLoginState(state: NetworkState?, r: Login.() -> Unit) {
    if (state is Login) state.r()
}

fun runInPlayState(state: NetworkState?, r: Play.() -> Unit) {
    if (state is Play) state.r()
}