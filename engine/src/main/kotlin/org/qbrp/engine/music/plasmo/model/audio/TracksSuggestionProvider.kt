package org.qbrp.engine.music.plasmo.model.audio

import org.qbrp.core.mc.commands.templates.ListProvider
import org.qbrp.engine.Engine
import org.qbrp.engine.music.MusicManagerAPI

class TracksSuggestionProvider() : ListProvider<String>({ Engine.getAPI<MusicManagerAPI>()?.getTracks()!!.map { it.name } })