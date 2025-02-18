package org.qbrp.engine.music.plasmo

import org.qbrp.engine.music.plasmo.model.audio.Playable
import org.qbrp.engine.music.plasmo.model.audio.PlayableDTO
import org.qbrp.engine.music.plasmo.model.audio.Playlist
import org.qbrp.engine.music.plasmo.model.audio.Track
import org.qbrp.system.database.DatabaseService
import su.plo.voice.api.server.PlasmoVoiceServer

class MusicDatabaseService(val db: DatabaseService) {
    fun openTracks(): List<Track> {
        return db.fetchAll("tracks", mapOf(), Track::class.java) as List<Track>
    }

    fun openPlaylists(): List<PlayableDTO> {
        val data = db.fetchAll("playlists", mapOf(), PlayableDTO::class.java) as List<PlayableDTO>
        return data
    }
    fun saveTrack(track: Track) {
        db.upsertObject<Track>("tracks", mapOf("name" to track.name), track)
    }
    fun savePlaylist(playlist: Playable) {
        db.upsertObject<PlayableDTO>("playlists", mapOf("name" to playlist.name), playlist.toDTO())
    }
    fun archivePlayable(playable: Playable) {
        db.delete("playlists", mapOf("name" to playable.name))
        db.upsertObject<PlayableDTO>("archive", mapOf("name" to playable.name), playable.toDTO())
    }
    fun archiveTrack(track: Track) {
        db.delete("tracks", mapOf("name" to track.name))
        db.upsertObject<Track>("archive", mapOf("name" to track.name), track)
    }
}
