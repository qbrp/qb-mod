package org.qbrp.engine.music

import org.qbrp.core.plasmo.PlasmoEventListener
import org.qbrp.engine.music.plasmo.playback.player.MusicPlayerManager
import su.plo.voice.api.event.EventSubscribe
import su.plo.voice.api.server.event.connection.UdpClientConnectEvent
import su.plo.voice.api.server.event.connection.UdpClientDisconnectedEvent

class MusicManagerEvents(val musicPlayerManager: MusicPlayerManager): PlasmoEventListener {

    @EventSubscribe
    fun onClientConnected(event: UdpClientConnectEvent) {
        musicPlayerManager.addPlayer(event.connection.player.instance.name)
    }

    @EventSubscribe
    fun onClientDisconnected(event: UdpClientDisconnectedEvent) {
        musicPlayerManager.removePlayer(event.connection.player.instance.name)
    }
}