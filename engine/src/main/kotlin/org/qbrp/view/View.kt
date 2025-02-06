package org.qbrp.view

import org.qbrp.view.hud.VanillaHud

//TODO: Убрать нахуй. Сделать всё на стороне клиента
class View {
    companion object {
        lateinit var vanillaHud: VanillaHud
    }

    fun initialize() {
        vanillaHud = VanillaHud()
    }
}