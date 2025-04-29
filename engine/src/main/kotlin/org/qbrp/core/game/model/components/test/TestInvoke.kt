package org.qbrp.core.game.model.components.test

import org.qbrp.core.game.model.components.Component
import org.qbrp.core.game.model.components.methods.InvokeReference

class TestInvoke(val call: InvokeReference): Component() {

    fun call() {
        call.invoke(requireState())
    }

}