package org.qbrp.main.core.storage

import kotlinx.coroutines.Deferred
import org.bson.Document
import org.qbrp.main.core.game.serialization.Identifiable

interface TableAccess {
    fun saveObject(id: String, json: String, fieldName: String = "id")
    fun getByField(name: String, value: Any): Deferred<Document?>
    fun getById(id: Any): Deferred<Document?>
    fun getAll(): Deferred<List<Document>>
    fun saveObject(obj: Identifiable, json: String, fieldName: String = "id") = saveObject(obj.id, json, fieldName)
}