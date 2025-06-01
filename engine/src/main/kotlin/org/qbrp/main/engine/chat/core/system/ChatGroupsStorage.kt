package org.qbrp.main.engine.chat.core.system

class ChatGroupsStorage(private val groups: MutableList<ChatGroup> = mutableListOf()) {
    fun getAllGroups(): List<ChatGroup> = groups
    fun addGroup(group: ChatGroup): ChatGroup { groups.add(group.apply { if(buildedComponents == null) buildComponents() }).also { return groups.last() } }
    fun loadGroups(groupsList: List<ChatGroup>) {
        groups.clear()
        groups.addAll(groupsList)
        groups.forEach { if(it.buildedComponents == null) it.buildComponents() }
    }
    fun getGroup(name: String): ChatGroup? = groups.find { it.name == name}
    fun contains(groupName: String): Boolean {
        return groups.any { it.name == groupName }
    }
}