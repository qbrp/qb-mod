package org.qbrp.engine.client.engine.chat.system

import config.ClientConfig
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.client.gui.hud.ChatHudLine
import org.qbrp.engine.chat.core.messages.ChatMessage
import org.qbrp.engine.client.engine.chat.ChatModuleClient
import org.qbrp.engine.client.engine.chat.system.events.TextUpdateCallback
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.atomic.AtomicReference
import kotlin.concurrent.thread
import kotlin.text.toMutableList

class UIMessageProvider(val name: String) : Provider {
    private val allMessages: MutableMap<String, HandledMessage> = mutableMapOf()
    private val cachedSnapshot: AtomicReference<MutableList<ChatHudLine.Visible>> = AtomicReference(mutableListOf())
    private val taskQueue = ConcurrentLinkedQueue<Runnable>()

    private var ticksCompleted = 0

    init {
        thread(isDaemon = true, name = "ChatUiUpdateThread") {
            while (true) {
                while (taskQueue.isNotEmpty()) {
                    taskQueue.poll()?.run()
                }
                Thread.sleep(10)
            }
        }

        ClientTickEvents.END_WORLD_TICK.register { client ->
            if (ticksCompleted++ > ClientConfig.chatTickRate) {
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
            if (message.getTags().getComponentData<String>("ui.name") == name) {
                HandledMessage.from(message).let {
                    allMessages[it.message.uuid] = it
                    updateCachedSnapshot()
                }
            }
        })
    }

    override fun onMessageEdited(uuid: String, message: ChatMessage, storage: MessageStorage) {
        TODO()
    }

    override fun onMessageDeleted(uuid: String, storage: MessageStorage) {
        TODO()
    }

    override fun onClear(storage: MessageStorage) {
        taskQueue.add(Runnable {
            allMessages.clear()
            cachedSnapshot.set(mutableListOf()) // Очищаем кэш
        })
    }

    private fun updateCachedSnapshot() {
        // Создаем новый список сообщений и пересчитываем кэш
        val newSnapshot = allMessages.values.toList()
        val processed = newSnapshot.reversed().flatMap { it.text }.toMutableList()
        cachedSnapshot.set(processed) // Атомарно обновляем кэш
    }
}