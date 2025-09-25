package org.qbrp.main.engine.physics.dynamics.chunk.factory

import org.qbrp.main.engine.physics.dynamics.ChunkConstants
import org.qbrp.main.engine.world.location.Position

class LoadSettings(val minHeight: Int = ChunkConstants.WORLD_MIN_HEIGHT,
                   val maxHeight: Int = ChunkConstants.WORLD_MAX_HEIGHT,)