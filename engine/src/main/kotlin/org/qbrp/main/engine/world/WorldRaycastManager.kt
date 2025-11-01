package org.qbrp.main.engine.world

import org.qbrp.main.engine.world.location.WorldPos

interface WorldRaycastManager {
     fun raycast(start: WorldPos, end: WorldPos, filter: RaycastFilter): RaycastResult
}