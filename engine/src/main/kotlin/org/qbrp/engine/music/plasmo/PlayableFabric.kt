package org.qbrp.engine.music.plasmo

import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.context.GlobalContext
import org.qbrp.engine.music.plasmo.model.audio.Playable
import org.qbrp.engine.music.plasmo.model.audio.PlayableDTO
import org.qbrp.engine.music.plasmo.model.audio.Playlist
import org.qbrp.engine.music.plasmo.model.audio.shadow.Shadow
import su.plo.voice.api.server.PlasmoVoiceServer

class PlayableFabric(val voiceServer: PlasmoVoiceServer, val storage: MusicStorage): KoinComponent {
    fun build(dto: PlayableDTO): Playable {
        return when (dto.type) {
            "playlist" -> buildPlaylist(dto)
            "shadow" -> buildShadow(dto)
            else -> throw NoSuchElementException("Тип данных ${dto.type} не найден")
        }
    }

    private fun buildPlaylist(dto: PlayableDTO): Playlist {
        return Playlist(
            dto.name,
            dto.selector,
            dto.priority,
            get(),
            get()
        ).apply {
            loadQueue(dto.queue)
            isManuallyDisabled = dto.isManuallyDisabled
        }
    }

    private fun buildShadow(dto: PlayableDTO): Shadow {
        return Shadow(
            dto.originalName,
            dto.name,
            dto.selector,
            dto.priority,
            get(),
            get()
        ).apply {
            loadQueue(storage.getPlayable(dto.name)!!.queue)
            isManuallyDisabled = dto.isManuallyDisabled
        }
    }

}