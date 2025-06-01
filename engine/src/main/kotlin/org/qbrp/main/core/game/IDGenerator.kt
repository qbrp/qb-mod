package org.qbrp.main.core.game

import java.util.prefs.Preferences

object IDGenerator {
    val prefs = Preferences.userNodeForPackage(IDGenerator::class.java)
    fun nextId(): Long {
        val id = prefs.getLong("lastId", 0L) + 1
        prefs.putLong("lastId", id)
        return id
    }

}