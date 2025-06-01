package org.qbrp.main.engine.music.plasmo.model.audio

import org.qbrp.main.core.mc.commands.templates.ListProvider
import org.qbrp.main.engine.Engine
import org.qbrp.main.engine.music.MusicManagerAPI

class TracksSuggestionProvider() : ListProvider<String>({ Engine.getAPI<MusicManagerAPI>()?.getTracks()!!.map { it.name } })