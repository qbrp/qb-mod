package core.server

import org.qbrp.main.core.mc.server.thread.ServerThread

class ServerThreadMock: ServerThread {
    override fun execute(runnable: Runnable) {
        runnable.run()
    }

    override fun assertOnServerThread() {}
}