package org.qbrp.client.core.keybinds

import net.minecraft.client.util.InputUtil
import org.qbrp.main.core.keybinds.KeybindId

interface ClientKeybindsAPI {
    fun registerKeybinding(
        action: String,
        code: Int,
        id: KeybindId,
        type: InputUtil.Type = InputUtil.Type.KEYSYM,
        category: String = "qbrp",
        modifiers: List<KeyBindModifier> = listOf(),
        direct: Boolean = false,
        method: () -> Unit
    )
    fun registerKeybinding(
        action: String,
        code: Int,
        id: KeybindId,
        onPress: () -> Unit,
        onRelease: () -> Unit,
        type: InputUtil.Type = InputUtil.Type.KEYSYM,
        category: String = "qbrp"
    )
}