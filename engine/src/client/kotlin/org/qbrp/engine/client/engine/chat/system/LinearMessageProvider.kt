package org.qbrp.engine.client.engine.chat.system

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.client.gui.hud.ChatHudLine
import org.qbrp.engine.chat.core.messages.ChatMessage
import org.qbrp.engine.client.engine.chat.ChatModuleClient
import org.qbrp.engine.client.engine.chat.system.events.TextUpdateCallback
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.atomic.AtomicReference
import kotlin.concurrent.thread

class LinearMessageProvider(val filter: (HandledMessage) -> Boolean = { true }) : Provider {
    private val allMessages: MutableMap<String, HandledMessage> = mutableMapOf()
    private val cachedSnapshot: AtomicReference<MutableList<ChatHudLine.Visible>> = AtomicReference(mutableListOf())
    private val taskQueue = ConcurrentLinkedQueue<Runnable>()

    private var ticksCompleted = 0
    init {
        thread(isDaemon = true, name = "ChatUpdateThread") {
            while (true) {
                while (taskQueue.isNotEmpty()) {
                    taskQueue.poll()?.run()
                }
                Thread.sleep(10)
            }
        }

        ClientTickEvents.END_WORLD_TICK.register { client ->
            if (ticksCompleted++ > ChatModuleClient.TEXT_UPDATE_TICK_RATE) {
                taskQueue.add(Runnable {
                    allMessages.values.forEach { line ->
                        TextUpdateCallback.EVENT.invoker().modifyText(line.text, line) ?: line.text
                    }
                    updateCachedSnapshot() }
                )
                ticksCompleted = 0
            }
        }
    }

    override fun provide(storage: MessageStorage): MutableList<ChatHudLine.Visible> {
        return cachedSnapshot.get()
    }

    override fun onMessageAdded(message: ChatMessage, storage: MessageStorage) {
        taskQueue.add(Runnable {
            HandledMessage.from(message).let {
                allMessages[it.message.uuid] = it
                updateCachedSnapshot()
            }
        })
    }

    override fun onMessageEdited(uuid: String, message: ChatMessage, storage: MessageStorage) {
        taskQueue.add(Runnable {
            val editedMessage = allMessages[uuid] ?: return@Runnable
            editedMessage.editText(editedMessage.update(message))
            updateCachedSnapshot()
        })
    }

    override fun onMessageDeleted(uuid: String, storage: MessageStorage) {
        taskQueue.add(Runnable {
            allMessages.remove(uuid)
            updateCachedSnapshot()
        })
    }

    override fun clear() {
        taskQueue.add(Runnable {
            allMessages.clear()
            cachedSnapshot.set(mutableListOf()) // Очищаем кэш
        })
    }

    private fun updateCachedSnapshot() {
        // Создаем новый список сообщений и пересчитываем кэш
        val newSnapshot = allMessages.values.toList()
        val processed = newSnapshot.reversed().filter { filter(it) }.flatMap { it.text }.toMutableList()
        cachedSnapshot.set(processed) // Атомарно обновляем кэш
    }
}