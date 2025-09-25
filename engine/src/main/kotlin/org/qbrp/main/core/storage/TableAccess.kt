package org.qbrp.main.core.storage

import kotlinx.coroutines.Deferred
import org.bson.Document
import org.qbrp.main.core.components.serialization.Identifiable

interface TableAccess {
    fun saveObject(id: String, json: String)
    fun getByField(name: String, value: Any): Deferred<Document?>
    fun getById(id: Any): Deferred<Document?>
    fun getAll(): Deferred<List<Document>>
    fun saveObject(obj: Identifiable, json: String) = saveObject(obj.id, json)
}