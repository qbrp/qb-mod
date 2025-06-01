package org.qbrp.main.engine.music

import org.qbrp.main.engine.music.plasmo.model.audio.Track
import org.qbrp.main.core.modules.ModuleAPI

interface MusicManagerAPI: ModuleAPI {
    fun getTracks(): List<Track>
}