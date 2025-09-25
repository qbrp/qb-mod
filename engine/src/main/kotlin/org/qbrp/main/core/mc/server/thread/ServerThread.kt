package org.qbrp.main.core.mc.server.thread

interface ServerThread: ThreadExecutor {
    fun assertOnServerThread()
}