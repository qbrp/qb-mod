package org.qbrp.core.game.model.objects

import org.qbrp.core.game.lifecycle.Lifecycle
import org.qbrp.core.game.model.components.methods.InvokeReference
import org.qbrp.core.game.model.components.test.TestInvoke
import org.qbrp.core.game.model.components.test.TestPrint

class TestObject(override val lifecycle: Lifecycle<BaseObject>): BaseObject(lifecycle = lifecycle) {

    init {
        state.addComponent(TestPrint())
        state.addComponent(TestInvoke(InvokeReference("TestPrint/print", listOf("Hello World!"))))
    }

    fun script() {
        state.getComponentOrThrow<TestInvoke>().call()
    }
}