package org.qbrp.main.core.game.model.objects

import org.qbrp.main.core.game.lifecycle.Lifecycle
import org.qbrp.main.core.game.model.components.methods.InvokeReference
import org.qbrp.main.core.game.model.components.test.TestInvoke
import org.qbrp.main.core.game.model.components.test.TestPrint

class TestObject(override val lifecycle: Lifecycle<BaseObject>): BaseObject(lifecycle = lifecycle) {

    init {
        state.addComponent(TestPrint())
        state.addComponent(TestInvoke(InvokeReference("TestPrint/print", listOf("Hello World!"))))
    }

    fun script() {
        state.getComponentOrThrow<TestInvoke>().call()
    }
}