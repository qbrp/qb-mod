package org.qbrp.engine.music

import org.qbrp.engine.music.plasmo.model.audio.Track
import org.qbrp.system.modules.ModuleAPI

interface MusicManagerAPI: ModuleAPI {
    fun getTracks(): List<Track>
}