package org.qbrp.core.groups

import org.qbrp.system.database.DatabaseService

class GroupsDatabase(val db: DatabaseService) {

    fun openGroups(): List<Group> {
        return db.fetchAll("chatGroups", mapOf(), Group::class.java) as List<Group>
    }

    fun saveGroup(group: Group) {
        db.upsertObject<Group>("chatGroups", mapOf("players" to group.players), group)
    }

    fun deleteGroup(group: Group) {
        db.delete("chatGroups", mapOf("players" to group.players))
    }
}
