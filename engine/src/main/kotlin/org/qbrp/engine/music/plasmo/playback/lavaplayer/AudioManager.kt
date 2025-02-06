package org.qbrp.engine.music.plasmo.playback.lavaplayer

import org.qbrp.core.resources.ServerResources
import org.qbrp.core.resources.data.StringData
import org.qbrp.system.utils.log.Loggers
import su.plo.voice.lavaplayer.libs.com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler
import su.plo.voice.lavaplayer.libs.com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager
import su.plo.voice.lavaplayer.libs.com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager
import su.plo.voice.lavaplayer.libs.com.sedmelluq.discord.lavaplayer.source.bandcamp.BandcampAudioSourceManager
import su.plo.voice.lavaplayer.libs.com.sedmelluq.discord.lavaplayer.source.beam.BeamAudioSourceManager
import su.plo.voice.lavaplayer.libs.com.sedmelluq.discord.lavaplayer.source.getyarn.GetyarnAudioSourceManager
import su.plo.voice.lavaplayer.libs.com.sedmelluq.discord.lavaplayer.source.http.HttpAudioSourceManager
import su.plo.voice.lavaplayer.libs.com.sedmelluq.discord.lavaplayer.source.soundcloud.SoundCloudAudioSourceManager
import su.plo.voice.lavaplayer.libs.com.sedmelluq.discord.lavaplayer.source.twitch.TwitchStreamAudioSourceManager
import su.plo.voice.lavaplayer.libs.com.sedmelluq.discord.lavaplayer.source.vimeo.VimeoAudioSourceManager
import su.plo.voice.lavaplayer.libs.com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import su.plo.voice.lavaplayer.libs.com.sedmelluq.discord.lavaplayer.track.AudioPlaylist
import su.plo.voice.lavaplayer.libs.com.sedmelluq.discord.lavaplayer.track.AudioTrack
import su.plo.voice.lavaplayer.libs.dev.lavalink.youtube.YoutubeAudioSourceManager
import java.util.concurrent.CompletableFuture
import kotlin.concurrent.fixedRateTimer

object AudioManager {
    val lavaPlayerManager: AudioPlayerManager = DefaultAudioPlayerManager()
    val noMatchesException = FriendlyException(
        "No matches",
        FriendlyException.Severity.COMMON,
        Exception("No matches")
    )
    val logger = Loggers.get("audioManager")

    fun getTrack(identifier: String): CompletableFuture<AudioTrack> {
        val future = CompletableFuture<AudioTrack>()
        lavaPlayerManager.loadItem(identifier, object : AudioLoadResultHandler {
            override fun trackLoaded(track: AudioTrack) {
                future.complete(track)
            }

            override fun playlistLoaded(playlist: AudioPlaylist) {
                if (playlist.selectedTrack == null) {
                    future.completeExceptionally(noMatchesException)
                    return
                }

                future.complete(playlist.selectedTrack)
            }

            override fun loadFailed(exception: FriendlyException) {
                future.completeExceptionally(exception)
            }

            override fun noMatches() {
                future.completeExceptionally(noMatchesException)
            }
        })
        return future
    }

    fun registerSources() {
        val youtubeClients = listOf(
            YoutubeClient.MUSIC,
            YoutubeClient.WEB,
            YoutubeClient.MWEB,
            YoutubeClient.WEBEMBEDDED
        )

        lavaPlayerManager.registerSourceManager(
            YoutubeAudioSourceManager(true, *youtubeClients.map { it.client.get() }.toTypedArray())
                .also { source ->
                    // Проверка токена и регистрация слушателя на его изменения
                    val refreshToken = ServerResources.getRootBranch().youtubeToken
                        .takeIf { it.path.toFile().isFile && it.path.toFile().exists() }
                        ?.let { (it.data as? StringData)?.string }
                        ?.trim()

                    source.useOauth2(refreshToken, false)
                    if (refreshToken == null) {
                        listenForTokenChange(source)
                    }
                }
        )
        lavaPlayerManager.registerSourceManager(SoundCloudAudioSourceManager.createDefault())
        lavaPlayerManager.registerSourceManager(BandcampAudioSourceManager())
        lavaPlayerManager.registerSourceManager(VimeoAudioSourceManager())
        lavaPlayerManager.registerSourceManager(TwitchStreamAudioSourceManager())
        lavaPlayerManager.registerSourceManager(BeamAudioSourceManager())
        lavaPlayerManager.registerSourceManager(GetyarnAudioSourceManager())
        lavaPlayerManager.registerSourceManager(HttpAudioSourceManager())
    }

    private fun listenForTokenChange(youtubeSourceManager: YoutubeAudioSourceManager) {
        val currentToken = youtubeSourceManager.oauth2RefreshToken
        fixedRateTimer(
            "[pv-addon-music-manager] [youtube-refresh-token-listener]",
            true,
            5000L,
            5000L
        ) {
            logger.log("Запрашивается новый ключ авторизации для YouTube (${youtubeSourceManager.oauth2RefreshToken})")
            val newToken = youtubeSourceManager.oauth2RefreshToken ?: return@fixedRateTimer
            if (currentToken == newToken) return@fixedRateTimer
            saveToken()
            cancel()
        }
    }

    private fun saveToken() {
        lavaPlayerManager.sourceManagers
            .filterIsInstance<YoutubeAudioSourceManager>()
            .firstOrNull()
            ?.takeIf { it.oauth2RefreshToken != null }
            ?.let {
                val refreshTokenFile = ServerResources.getRootBranch().youtubeToken
                (refreshTokenFile.data as StringData).string = it.oauth2RefreshToken!!
                refreshTokenFile.save()
            }
    }
}