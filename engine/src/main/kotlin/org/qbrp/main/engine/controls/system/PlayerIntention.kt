package org.qbrp.main.engine.controls.system

import org.qbrp.main.engine.controls.entity.interactions.Intent

data class PlayerIntention(val intent: Intent, val alternate: Boolean)