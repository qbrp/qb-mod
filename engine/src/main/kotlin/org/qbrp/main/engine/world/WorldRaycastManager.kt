package org.qbrp.main.engine.world

import org.qbrp.main.engine.world.location.Position

interface WorldRaycastManager {
     fun raycast(start: Position, end: Position, filter: RaycastFilter): RaycastResult
}