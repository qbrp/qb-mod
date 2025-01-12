package org.qbrp.plasmo.controller

import com.mojang.brigadier.context.CommandContext
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.Text
import org.qbrp.plasmo.model.audio.Playlist
import org.qbrp.plasmo.model.audio.Track
import org.qbrp.plasmo.model.priority.Priorities
import org.qbrp.plasmo.model.priority.Priority
import org.qbrp.plasmo.model.selectors.Selector
import org.qbrp.plasmo.model.selectors.Selectors
import org.qbrp.plasmo.MusicStorage
import kotlin.reflect.KClass

class PlaylistEditor(val context: CommandContext<ServerCommandSource>) {
    var playlist: Playlist? = null
    var track: Track? = null
    var selector: Selector? = null
    var priority: Priority? = null

    fun getPlaylist() {
        val name = getArgument("playlistName", String::class)
        if (name == null) return
        playlist = MusicStorage.getPlaylist(name) ?: run { error("Плейлист $name не найден"); return }
    }

    fun getTrack() {
        val name = getArgument("trackName", String::class)
        if (name == null) return
        if (!MusicStorage.isTrackExists(name)) { error("Трек $name не найден"); return }
        track = MusicStorage.getTrack(name)
    }

    fun getSelector() {
        try {
        val selectorName = getArgument("selector", String::class)
        val selectorArgs = getArgument("selectorArgs", String::class)
        if (selectorName == null || selectorArgs == null) { println("Да нет аргументов"); return }
            selector = Selectors.createSelector(selectorName, listOf(selectorArgs)) ?: run {
                error("Некорректный селектор: $selectorName")
                return
            }
        } catch (e: Exception) {
            error("Ошибка при создании селектора: ${e.message}")
        }
    }

    fun getPriority() {
        val priorityName = getArgument("priority", String::class)
        if (priorityName == null) return
        priority = Priorities.getPriority(priorityName) ?: run {
            error("Некорректный приоритет: $priorityName")
            return
        }
    }

    fun process(action: (Playlist?, Track?, Selector?, Priority?) -> Unit): PlaylistEditor {
        action(playlist, track, selector, priority)
        return this
    }

    fun error(message: String) {
        context.source.sendError(Text.literal(message))
    }

    fun feedback(message: String) {
        context.source.sendMessage(Text.literal(message))
    }

    private fun <T : Any> getArgument(argument: String, clazz: KClass<T>): T? {
        return try {
            context.getArgument(argument, clazz.java)
        } catch (e: Exception) {
            error("Ошибка при обработке аргумента $argument: ${e.message}")
            null
        }
    }
}
