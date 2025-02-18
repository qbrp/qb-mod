package org.qbrp.core.groups

import org.qbrp.system.database.DatabaseService

class GroupsDatabase(val db: DatabaseService) {

    fun openGroups(): List<Group> {
        return db.fetchAll("groups", mapOf(), Group::class.java) as List<Group>
    }

    fun saveGroup(group: Group) {
        db.upsertObject<Group>("groups", mapOf("players" to group.players), group)
    }

    fun deleteGroup(group: Group) {
        db.delete("groups", mapOf("players" to group.players))
    }
}
