package org.qbrp.client.core.keybinds

import net.minecraft.client.util.InputUtil

interface ClientKeybindsAPI {
    fun registerKeybinding(action: String, code: Int, id: String, method: () -> Unit, type: InputUtil.Type = InputUtil.Type.KEYSYM, category: String = "qbrp")
}