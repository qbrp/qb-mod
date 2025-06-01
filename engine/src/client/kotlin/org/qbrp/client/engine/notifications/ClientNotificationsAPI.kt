package org.qbrp.client.engine.notifications

interface ClientNotificationsAPI {
    fun sendSystemMessage(title: String, message: String, titleColor: String = "<yellow>")
}