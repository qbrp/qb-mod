package org.qbrp.main.core.notifications.entity

import org.qbrp.main.core.mc.text.MinecraftText
import org.qbrp.main.engine.text.components.TextComponent
import org.qbrp.main.engine.text.literal

@ConsistentCopyVisibility
data class Notification internal constructor(val title: String, val text: TextComponent, val type: NotificationType) {
    companion object {
        fun error(title: String, context: String, exception: Exception): Notification {
            return Notification(title,
                literal("Контекст: $context. ${exception.message ?: "Неизвестная ошибка."}"),
                NotificationType.ERROR)
        }
        fun info(title: String, text: TextComponent): Notification {
            return Notification(title, text, NotificationType.INFO)
        }
    }
}

enum class NotificationType {
    ERROR, INFO
}
