package org.qbrp.plasmo

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import org.qbrp.core.resources.ServerResources
import org.qbrp.plasmo.model.Playable
import org.qbrp.plasmo.model.Region
import org.qbrp.plasmo.model.audio.Playlist
import org.qbrp.plasmo.model.audio.Track
import org.qbrp.plasmo.model.priority.Priorities
import org.qbrp.plasmo.model.priority.Priority
import org.qbrp.plasmo.model.selectors.Group
import org.qbrp.plasmo.model.selectors.RegionSelector
import org.qbrp.plasmo.model.selectors.Selector
import org.qbrp.system.database.DatabaseService
import kotlin.concurrent.fixedRateTimer

object MusicStorage {
    private val tracks = mutableMapOf<String, Track>()
    val groups = mutableListOf<Group>()
    private val regions = mutableListOf<Region>(Region("world"))
    private val playable = mutableListOf<Playable>()
    private val dbService = MusicDatabaseService(DatabaseService(ServerResources.root.config.databases.nodeUri, ServerResources.root.config.databases.music))

    fun load() {
        dbService.db.connect()
        dbService.openTracks().forEach { track -> tracks[track.name] = track }
        playable.addAll(dbService.openPlaylists())
        println(playable)
    }

    fun startSaveLifecycle() {
        fixedRateTimer(
            name = "[qbrp/Plasmo] [MusicStorage]",
            initialDelay = 0,
            period = 10000,
            daemon = true
        ) {
            try {
                tracks.values.forEach { dbService.saveTrack(it) }
                playable.forEach { dbService.savePlaylist(it as Playlist) }
                regions.forEach { dbService.saveRegion(it) }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun addDefaultPlaylist() {
        if (getPlaylist("Глобальный") == null) {
            addPlaylist("Глобальный", RegionSelector("world"), Priorities.lowest())
        }
    }

    fun addRegion(region: Region) = regions.add(region)
    fun getRegion(name: String): Region? { return regions.find { it.name == name } }
    fun removeRegion(region: Region) = regions.remove(region)

    fun isTrackExists(name: String): Boolean { return tracks.containsKey(name) }
    fun getTrack(name: String): Track = tracks[name]!!
    fun getAllTracks(): List<Track> = tracks.values.toList()
    fun deleteTrack(name: String) = tracks.remove(name)
    fun getDefaultPlaylist(): Playlist = playable.first() as Playlist

    fun getAllPlaylists(): List<Playable> = playable.toList()
    fun getPlaylist(name: String): Playlist? = playable.first { (it as Playlist).name == name } as Playlist?

    fun addTrack(name: String, link: String, cycle: Int = 1): Track {
        tracks[name] = (Track(link, name, cycle))
        return tracks[name]!!
    }

    fun addPlaylist(name: String, selector: Selector, priority: Priority, cycle: Int = -1) {
        playable.add(Playlist(name, selector, priority, cycle))
    }

    class MusicDatabaseService(val db: DatabaseService) {
        fun openTracks(): List<Track> {
            return db.fetchAll("tracks", mapOf(), Track::class.java).also { println(it)} as List<Track>
        }
        fun openPlaylists(): List<Playlist> {
            return db.fetchAll("playlists", mapOf(), Playlist::class.java) as List<Playlist>
        }
        fun openRegions(): List<Region> {
            return db.fetchAll("regions", mapOf(), Region::class.java) as List<Region>
        }
        fun saveTrack(track: Track) {
            db.upsertObject<Track>("tracks", mapOf("name" to track.name), track)
        }
        fun savePlaylist(playlist: Playlist) {
            db.upsertObject<Playlist>("playlists", mapOf("name" to playlist.name), playlist)
        }
        fun saveRegion(region: Region) {
            db.upsertObject<Region>("regions", mapOf("name" to region.name), region)
        }
    }
}