package org.qbrp.main.engine.controls.entity.display

import org.qbrp.main.engine.text.components.TextComponent

@JvmInline
value class InteractableName<T: TextComponent>(val text: T): TextComponent by text