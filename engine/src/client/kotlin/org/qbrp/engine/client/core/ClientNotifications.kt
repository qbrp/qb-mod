package org.qbrp.engine.client.core

interface ClientNotifications {
    fun sendSystemMessage(title: String, message: String, titleColor: String = "<yellow>")
}