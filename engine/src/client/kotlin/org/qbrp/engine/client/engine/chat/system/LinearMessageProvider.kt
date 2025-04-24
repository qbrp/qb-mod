package org.qbrp.engine.client.engine.chat.system

import config.ClientConfig
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.hud.ChatHudLine
import org.qbrp.engine.chat.core.messages.ChatMessage
import org.qbrp.engine.client.engine.chat.ChatModuleClient
import org.qbrp.engine.client.engine.chat.system.events.TextUpdateCallback
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.atomic.AtomicReference
import kotlin.concurrent.thread

class LinearMessageProvider(
    // Мапа с фильтрами: ключ — название фильтра, значение — лямбда, фильтрующая сообщения.
    val filters: MutableMap<String, (HandledMessage) -> Boolean> = mutableMapOf()
) : Provider {
    private val allMessages: MutableMap<String, HandledMessage> = mutableMapOf()
    private val cachedMessages: MutableList<ChatHudLine.Visible> = mutableListOf()
    private val cachedSnapshot: AtomicReference<MutableList<ChatHudLine.Visible>> = AtomicReference(mutableListOf())
    private val taskQueue = ConcurrentLinkedQueue<Runnable>()

    private var ticksCompleted = 0
    init {
        thread(isDaemon = true, name = "ChatUpdateThread") {
            while (true) {
                while (taskQueue.isNotEmpty()) {
                    taskQueue.poll()?.run()
                }
                Thread.sleep(5)
            }
        }

        ClientTickEvents.END_WORLD_TICK.register { client ->
            if (ticksCompleted++ > ClientConfig.chatTickRate && ClientConfig.chatTickRate != -1) {
                taskQueue.add(Runnable {
                    allMessages.values.forEach { line ->
                        TextUpdateCallback.EVENT.invoker().modifyText(line.text, line) ?: line.text
                    }
                    updateCachedSnapshot()
                })
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

    override fun onClear(storage: MessageStorage) {
        taskQueue.add(Runnable {
            allMessages.clear()
            cachedMessages.clear()
            storage.getMessages(0, 2000).forEach {
                onMessageAdded(it, storage)
            }
            cachedSnapshot.set(mutableListOf()) // Очищаем кэш
        })
    }

    private fun updateCachedSnapshot() {
        // Создаем новый список сообщений и пересчитываем кэш
        if (ClientConfig.handleMessagesOnReceive) {
            val newSnapshot = allMessages.values.toList()
            if (!ClientConfig.filterHandledMessages) {
                // Если мапа фильтров пуста, то берем все сообщения, иначе оставляем только те, которые проходят все фильтры.
                val processed = newSnapshot.reversed().filter {
                    if (filters.isEmpty()) true else filters.values.all { predicate -> predicate(it) }
                }.flatMap { it.text }.toMutableList()
                cachedSnapshot.set(processed) // Атомарно обновляем кэш
            } else {
                // Если мапа фильтров пуста, то берем все сообщения, иначе оставляем только те, которые проходят все фильтры.
                val processed = newSnapshot.reversed().flatMap { it.text }.toMutableList()
                cachedSnapshot.set(processed) // Атомарно обновляем кэш
            }
        } else {
            cachedSnapshot.set(cachedMessages.toMutableList())
        }
    }
}
