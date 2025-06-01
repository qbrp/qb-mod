package org.qbrp.main.core.storage

interface StorageAPI {
    fun getTable(name: String): Table
}