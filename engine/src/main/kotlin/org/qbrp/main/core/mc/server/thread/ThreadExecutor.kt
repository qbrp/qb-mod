package org.qbrp.main.core.mc.server.thread

interface ThreadExecutor {
    fun execute(runnable: Runnable)
}
