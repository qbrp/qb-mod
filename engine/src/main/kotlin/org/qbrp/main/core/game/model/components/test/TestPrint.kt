package org.qbrp.main.core.game.model.components.test

import org.qbrp.main.core.game.model.components.Component

class TestPrint: Component() {

    fun print(string: String) {
        println(string)
    }

}