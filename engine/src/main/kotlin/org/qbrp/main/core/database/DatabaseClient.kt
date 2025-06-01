package org.qbrp.main.core.database

interface DatabaseClient<T> {
    fun getDatabase(dbName: String): T
}