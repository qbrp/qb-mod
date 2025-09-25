package org.qbrp.main.core.storage

interface TableRegistry {
    fun getTable(name: String): Table
}