package org.qbrp.engine.music.plasmo.controller.view

import net.minecraft.text.ClickEvent
import net.minecraft.text.Text
import org.qbrp.engine.Engine
import org.qbrp.engine.music.plasmo.model.audio.Playable
import org.qbrp.engine.music.plasmo.model.audio.playback.PlayerSession
import org.qbrp.system.utils.format.Format.formatMinecraft

class PlaylistView(var playable: Playable, var session: PlayerSession? = null) : View {

    override fun getText(): Text {
        return getHeader()
    }

    private fun getHeader(): Text {
        val cycleComponent = if (playable.queue.repeats != -1) "&6(${playable.queue.currentRepeat} / ${playable.queue.repeats})" else ""
        val selectorComponent = createClickableSelectorComponent()
        val priorityComponent = createClickablePriorityComponent()
        val line = if (playable.queue.getCurrentTrack() != null && session != null) {
            val currentPosition = session!!.radio?.getCurrentPosition() ?: 10
            val trackEndTimestamp = playable.queue.getCurrentTrack()!!.endTimestamp.toInt()
            createProgressLine(currentPosition, trackEndTimestamp, 10, "━", "⬤")
        } else {
            "".formatMinecraft()
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
                .also { e.printStackTrace() }
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
            .append(createPlayButton().toText())
            .append(Text.literal(" "))
            .append(createStopButton().toText())
    }

    private fun createClickableTrackButton(index: Int, name: String): ClickableButton {
        return MusicClickableButton(
            label = name,
            color = 0xFFFFFF, // Голубой
            command = """/playlists edit "${playable.name}" playback pos $index""",
            hoverText = "Поставить трек",
            action = ClickEvent.Action.RUN_COMMAND,
            playlist = playable,
        )
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
            val track = Engine.musicManagerModule.storage.getTrackOrThrow(trackLine)
            val duration = MusicViewCommand.formatTime(track?.endTimestamp?.toInt() ?: 1 * (track?.loops ?: 1))
            val trackText = createTrackLine(index, trackLine, duration, track.loops.toString())
            result.append(trackText)
        }
        return result
    }

    private fun createTrackLine(index: Int, trackName: String, duration: String, loops: String): Text {
        val dot = if (playable.queue.currentTrackIndex == index) "&a$index:" else "&7$index"
        val loops = if (loops == "1" || loops == "0" ) "" else "&6($loops)"
        return Text.literal("")
            .append(dot.formatMinecraft())
            .append(" ")
            .append((createClickableTrackButton(index, trackName)).toText())
            .append(" ")
            .append("($duration) $loops".formatMinecraft())
            .append(" ")
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
            command = """/playlists edit "${playable.name}" queue moveUnary 1""",
            hoverText = "Переместить трек вверх",
            action = ClickEvent.Action.RUN_COMMAND,
            playlist = playable,
        )
    }

    private fun createMoveTrackDownButton(index: Int): ClickableButton {
        return MusicClickableButton(
            label = "[⬇]",
            color = 0x55FF55, // Зеленый
            command = """/playlists edit "${playable.name}" queue moveUnary -1""",
            hoverText = "Переместить трек вниз",
            action = ClickEvent.Action.RUN_COMMAND,
            playlist = playable,
        )
    }
}
