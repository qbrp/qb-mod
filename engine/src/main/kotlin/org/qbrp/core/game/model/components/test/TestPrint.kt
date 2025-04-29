package org.qbrp.core.game.model.components.test

import org.qbrp.core.game.model.components.Component

class TestPrint: Component() {

    fun print(string: String) {
        println(string)
    }

}