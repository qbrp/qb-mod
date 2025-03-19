package org.qbrp.core.groups

import org.qbrp.core.game.commands.templates.ListProvider
import org.qbrp.core.game.registry.CommandsRepository
import org.qbrp.core.resources.ServerResources
import org.qbrp.system.database.DatabaseService
import org.qbrp.system.utils.log.Loggers

object Groups {
    private val groups = mutableListOf<Group>()
    private val databaseService = GroupsDatabase(
        DatabaseService(
            ServerResources.getConfig().databases.nodeUri,
            ServerResources.getConfig().databases.groups
        ).apply { connect() }
    )
    init {
        CommandsRepository.add(GroupsCommand())
    }

    private val logger = Loggers.get("chatGroups")
    fun getGroups() = groups
    fun getGroup(name: String) = groups.find { it.name == name }

    fun saveAll() {
        groups.forEach { databaseService.saveGroup(it) }
    }

    fun openGroups() {
        groups.addAll(databaseService.openGroups())
        logger.log("Загружено <<${groups.size}>> групп")
    }

    fun createGroup(name: String, players: List<String> = emptyList()) {
        createGroup(Group(name, players.toMutableList()))
    }

    fun createGroup(group: Group) {
        groups.removeIf { it.name == group.name }
        groups.add(group)
        saveAll()
    }

    fun removeGroup(group: Group) {
        databaseService.deleteGroup(group)
        groups.remove(group)
    }

    class GroupsProvider : ListProvider<String>({ getGroups().map { it.name } })
}
