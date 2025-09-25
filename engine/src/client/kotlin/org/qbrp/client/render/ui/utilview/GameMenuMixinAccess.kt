package org.qbrp.client.render.ui.utilview

import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object GameMenuMixinAccess: KoinComponent {
    private val menu: GameMenu by inject()

    fun openGameMenuScreen() {
        menu.open()
    }
}