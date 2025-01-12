package org.qbrp.plasmo.controller.view

import net.minecraft.text.ClickEvent
import net.minecraft.text.Style
import net.minecraft.text.Text
import org.qbrp.plasmo.model.audio.Playlist
import org.qbrp.plasmo.model.selectors.Selectors
import org.qbrp.plasmo.MusicStorage
class PlaylistView(val playlist: Playlist) : View {

    override fun getText(): Text {
        return getHeader()
    }

    private fun getHeader(): Text {
        val cycleComponent = if (playlist.cycle != -1) "&6(${playlist.playback.currentCycle} / ${playlist.cycle})" else ""
        val selectorComponent = createClickableSelectorComponent()
        val priorityComponent = createClickablePriorityComponent()
        val line = createProgressLine(playlist.playback.currentTime, playlist.getCurrentTrack().endTimestamp.toInt(), 10, "━", "⬤")
        val tracks = getTracks()

        return Text.literal("")
            .append(createClickablePlaylistName())
            .append(" $cycleComponent  ")
            .append(selectorComponent)
            .append("  ")
            .append(priorityComponent)
            .append("\n\n")
            .append(tracks)
            .append("\n")
            .append(line)
    }

    private fun createClickablePlaylistName(): Text {
        return MusicClickableButton(
            label = playlist.name,
            color = 0x55FFFF, // Голубой
            command = """/playlists edit "${playlist.name}"""",
            hoverText = "Редактировать плейлист",
            playlist = playlist,
        ).toText()
    }

    private fun createClickableSelectorComponent(): Text {
        val selectorName = Selectors.getSelectorName(playlist.selector::class)?.replaceFirstChar { it.uppercase() } ?: "Unknown"
        val selectorParams = playlist.selector.params.joinToString()
        return MusicClickableButton(
            label = "Ⓢ $selectorName: $selectorParams",
            color = 0x55FF55, // Зеленый
            command = """/playlists edit "${playlist.name}" settings selector""",
            hoverText = "Редактировать селектор",
            playlist = playlist,
        ).toText()
    }

    private fun createClickablePriorityComponent(): Text {
        return MusicClickableButton(
            label = "Ⓟ ${playlist.priority.name}",
            color = 0x55FF55, // Зеленый
            command = """/playlists edit "${playlist.name}" settings priority""",
            hoverText = "Редактировать приоритет",
            playlist = playlist,
        ).toText()
    }

    private fun createProgressLine(position: Int, duration: Int, size: Int, lineSymbol: String, dotSymbol: String): Text {
        val progressIndex = (position * size) / duration
        val progressLine = buildString {
            for (i in 0 until size) {
                append(if (i == progressIndex) dotSymbol else lineSymbol)
            }
        }

        return Text.literal("§x$progressLine ")
            .append(createStopButton().toText())
            .append(Text.literal(" "))
            .append(createPlayButton().toText())
    }

    private fun createStopButton(): ClickableButton {
        return MusicClickableButton(
            label = "[О]",
            color = 0xFF5555, // Красный
            command = """/playlists stop "${playlist.name}"""",
            hoverText = "Остановить плейлист",
            action = ClickEvent.Action.RUN_COMMAND,
            playlist = playlist,
        )
    }

    private fun createPlayButton(): ClickableButton {
        return MusicClickableButton(
            label = "[И]",
            color = 0x55FF55, // Зеленый
            command = """/playlists play "${playlist.name}"""",
            hoverText = "Запустить плейлист",
            action = ClickEvent.Action.RUN_COMMAND,
            playlist = playlist,
        )
    }

    private fun getTracks(): Text {
        val result = Text.literal("")
        playlist.tracks.forEachIndexed { index, trackLine ->
            val track = MusicStorage.getTrack(trackLine)
            val duration = ViewCommands.formatTime(track.endTimestamp.toInt() * track.cycle)
            val trackText = createTrackLine(index, trackLine, duration)
            result.append(trackText)
        }
        return result
    }

    private fun createTrackLine(index: Int, trackName: String, duration: String): Text {
        return Text.literal("")
            .append(Text.literal("▪ $trackName ($duration) ")
                .setStyle(Style.EMPTY.withColor(0xFFFFFF)))
            .append(createRemoveTrackButton(index).toText())
            .append(" ")
            .append(createMoveTrackUpButton(index).toText())
            .append(" ")
            .append(createMoveTrackDownButton(index).toText())
            .append("\n")
    }

    private fun createRemoveTrackButton(index: Int): ClickableButton {
        return MusicClickableButton(
            label = "[✖]",
            color = 0xFF5555, // Красный
            command = """/playlists edit "${playlist.name}" queue remove $index""",
            hoverText = "Удалить трек",
            action = ClickEvent.Action.RUN_COMMAND,
            playlist = playlist,
        )
    }

    private fun createMoveTrackUpButton(index: Int): ClickableButton {
        return MusicClickableButton(
            label = "[⬆]",
            color = 0x55FF55, // Зеленый
            command = """/playlists edit "${playlist.name}" queue move $index ${index - 1}""",
            hoverText = "Переместить трек вверх",
            action = ClickEvent.Action.RUN_COMMAND,
            playlist = playlist,
        )
    }

    private fun createMoveTrackDownButton(index: Int): ClickableButton {
        return MusicClickableButton(
            label = "[⬇]",
            color = 0x55FF55, // Зеленый
            command = """/playlists edit "${playlist.name}" queue move $index ${index + 1}""",
            hoverText = "Переместить трек вниз",
            action = ClickEvent.Action.RUN_COMMAND,
            playlist = playlist,
        )
    }
}

