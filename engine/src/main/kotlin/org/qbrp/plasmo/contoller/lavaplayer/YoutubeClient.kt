package org.qbrp.plasmo.contoller.lavaplayer

import su.plo.voice.lavaplayer.libs.dev.lavalink.youtube.clients.Android
import su.plo.voice.lavaplayer.libs.dev.lavalink.youtube.clients.MWeb
import su.plo.voice.lavaplayer.libs.dev.lavalink.youtube.clients.Music
import su.plo.voice.lavaplayer.libs.dev.lavalink.youtube.clients.Web
import su.plo.voice.lavaplayer.libs.dev.lavalink.youtube.clients.WebEmbedded
import su.plo.voice.lavaplayer.libs.dev.lavalink.youtube.clients.skeleton.Client
import java.util.function.Supplier

enum class YoutubeClient(
    val client: Supplier<Client>
) {
    MUSIC({ Music() }),
    WEB({ Web() }),
    MWEB({ MWeb() }),
    WEBEMBEDDED({ WebEmbedded() }),
    ANDROID({ Android() }),
}