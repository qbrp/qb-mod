package org.qbrp.main.core.keybinds

interface ServerKeybindsAPI {
    fun registerKeybindReceiver(id: String)
    fun registerKeyBind(id: String, defaultKey: Int, name: String)
    fun composeServerKeybindsInfo()
}