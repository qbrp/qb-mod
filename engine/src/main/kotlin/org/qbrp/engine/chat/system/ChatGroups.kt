package org.qbrp.engine.chat.system

class ChatGroups(private val groups: MutableList<ChatGroup> = mutableListOf()) {
    fun getAllGroups(): List<ChatGroup> = groups
    fun loadGroups(groupsList: List<ChatGroup>) {
        groups.clear()
        groups.addAll(groupsList)
    }
    fun getGroup(name: String): ChatGroup? = groups.find { it.name == name}
    fun contains(groupName: String): Boolean {
        return groups.any { it.name == groupName }
    }
}