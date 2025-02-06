package org.qbrp.engine.music.plasmo.controller.view

import net.minecraft.text.ClickEvent
import net.minecraft.text.Style
import net.minecraft.text.Text
import org.qbrp.engine.Engine
import org.qbrp.engine.music.plasmo.model.audio.Playable
import org.qbrp.system.utils.format.Format.formatMinecraft

class PlaylistView(val playable: Playable) : View {

    override fun getText(): Text {
        return getHeader()
    }

    private fun getHeader(): Text {
        val cycleComponent = if (playable.queue.repeats != -1) "&6(${playable.queue.currentRepeat} / ${playable.queue.repeats})" else ""
        val selectorComponent = createClickableSelectorComponent()
        val priorityComponent = createClickablePriorityComponent()
        val line = if (playable.queue.tracks.isNotEmpty() || playable.queue.radio != null) {
            val currentPosition = playable.queue.radio?.getCurrentPosition() ?: 10
            val trackEndTimestamp = playable.queue.getCurrentTrack()?.endTimestamp?.toInt() ?: 10
            createProgressLine(currentPosition, trackEndTimestamp, 10, "━", "⬤")
        } else {
            "В данный момент ни один из треков не проигрывается".formatMinecraft()
        }
        val tracks = getTracks()

        return try { Text.literal("")
            .append(createClickablePlaylistName())
            .append(" $cycleComponent  ".formatMinecraft())
            .append(selectorComponent)
            .append("  ")
            .append(priorityComponent)
            .append("  ")
            .append(createEnableButton().toText())
            .append(createDisableButton().toText())
            .append("\n\n")
            .append(tracks)
            .append("\n")
            .append(line)
        } catch (e: Exception) {
            Text.literal("Плейлист ${playable.name}")
        }
    }

    private fun createClickablePlaylistName(): Text {
        return MusicClickableButton(
            label = playable.name,
            color = 0x55FFFF, // Голубой
            command = """/playlists edit "${playable.name}"""",
            hoverText = "Редактировать плейлист",
            playlist = playable,
        ).toText()
    }

    private fun createClickableSelectorComponent(): Text {
        val selectorName = playable.selector.type.replaceFirstChar { it.uppercase() }
        val selectorParams = playable.selector.params.joinToString()
        return MusicClickableButton(
            label = "Ⓢ $selectorName: $selectorParams",
            color = 0x55FF55, // Зеленый
            command = """/playlists edit "${playable.name}" settings selector""",
            hoverText = "Редактировать селектор",
            playlist = playable,
        ).toText()
    }

    private fun createClickablePriorityComponent(): Text {
        return MusicClickableButton(
            label = "Ⓟ ${playable.priority.name}",
            color = 0x55FF55, // Зеленый
            command = """/playlists edit "${playable.name}" settings priority""",
            hoverText = "Редактировать приоритет",
            playlist = playable,
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
            .append(createBackButton().toText())
            .append(Text.literal(" "))
            .append(createPlayButton().toText())
            .append(Text.literal(" "))
            .append(createStopButton().toText())
            .append(Text.literal(" "))
            .append(createNextButton().toText())
    }

    private fun createNextButton(): ClickableButton {
        return MusicClickableButton(
            label = "[→]",
            color = 0x55FFFF, // Голубой
            command = """/playlists edit "${playable.name}" playback pos ${playable.queue.currentTrackIndex + 1}""",
            hoverText = "Следующий трек",
            action = ClickEvent.Action.RUN_COMMAND,
            playlist = playable,
        )
    }

    private fun createBackButton(): ClickableButton {
        return MusicClickableButton(
            label = "[←]",
            color = 0x55FFFF, // Голубой
            command = """/playlists edit "${playable.name}" playback pos ${playable.queue.currentTrackIndex - 1}""",
            hoverText = "Предыдущий трек",
            action = ClickEvent.Action.RUN_COMMAND,
            playlist = playable,
        )
    }

    private fun createStopButton(): ClickableButton {
        return MusicClickableButton(
            label = "[О]",
            color = 0xFF5555, // Красный
            command = """/playlists stop "${playable.name}"""",
            hoverText = "Остановить плейлист",
            action = ClickEvent.Action.RUN_COMMAND,
            playlist = playable,
        )
    }

    private fun createPlayButton(): ClickableButton {
        return MusicClickableButton(
            label = "[И]",
            color = 0x55FF55, // Зеленый
            command = """/playlists play "${playable.name}"""",
            hoverText = "Запустить плейлист",
            action = ClickEvent.Action.RUN_COMMAND,
            playlist = playable,
        )
    }

    private fun createEnableButton(): ClickableButton {
        return MusicClickableButton(
            label = "[ВКЛ /",
            color = 0x55FF55, // Зеленый
            command = """/playlists edit "${playable.name}" playback enabled true""",
            hoverText = "Включить плейлист",
            action = ClickEvent.Action.RUN_COMMAND,
            playlist = playable,
        )
    }

    private fun createDisableButton(): ClickableButton {
        return MusicClickableButton(
            label = " ВЫКЛ]",
            color = 0xFF5555, // Красный
            command = """/playlists edit "${playable.name}" playback enabled false""",
            hoverText = "Выключить плейлист",
            action = ClickEvent.Action.RUN_COMMAND,
            playlist = playable,
        )
    }

    private fun getTracks(): Text {
        val result = Text.literal("")
        playable.queue.tracks.forEachIndexed { index, trackLine ->
            val track = Engine.musicManagerModule.storage.getTrack(trackLine)
            val duration = MusicViewCommand.formatTime(track?.endTimestamp?.toInt() ?: 1 * (track?.cycle ?: 1))
            val trackText = createTrackLine(index, trackLine, duration)
            result.append(trackText)
        }
        return result
    }

    private fun createTrackLine(index: Int, trackName: String, duration: String): Text {
        val dot = if (playable.queue.currentTrackIndex == index) "&c▪" else "▪"
        return Text.literal("")
            .append(dot.formatMinecraft())
            .append(Text.literal(" $trackName ($duration) ")
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
            command = """/playlists edit "${playable.name}" queue remove $index""",
            hoverText = "Удалить трек",
            action = ClickEvent.Action.RUN_COMMAND,
            playlist = playable,
        )
    }

    private fun createMoveTrackUpButton(index: Int): ClickableButton {
        return MusicClickableButton(
            label = "[⬆]",
            color = 0x55FF55, // Зеленый
            command = """/playlists edit "${playable.name}" queue move $index ${index - 1}""",
            hoverText = "Переместить трек вверх",
            action = ClickEvent.Action.RUN_COMMAND,
            playlist = playable,
        )
    }

    private fun createMoveTrackDownButton(index: Int): ClickableButton {
        return MusicClickableButton(
            label = "[⬇]",
            color = 0x55FF55, // Зеленый
            command = """/playlists edit "${playable.name}" queue move $index ${index + 1}""",
            hoverText = "Переместить трек вниз",
            action = ClickEvent.Action.RUN_COMMAND,
            playlist = playable,
        )
    }
}
