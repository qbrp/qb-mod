package org.qbrp.engine.client.render.screen.chat

object MessageComponentsAssociations {
    private val associations = mapOf(
        "mention" to "пинг",
        "group" to "чат"
    )

    fun get(component: String): String? {
        return associations[component]
    }
}