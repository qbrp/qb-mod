package core

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import org.bson.Document
import org.qbrp.main.core.storage.TableAccess

class TableMock: TableAccess {
    private val documents: MutableMap<String, Document> = mutableMapOf()
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    override fun saveObject(id: String, json: String) {
        documents[id] = Document.parse(json)
    }

    override fun getByField(name: String, value: Any): Deferred<Document?> {
         return coroutineScope.async { documents.values.firstOrNull { it.containsKey(name) && it.get(name) == value } }
    }

    override fun getById(id: Any): Deferred<Document?> {
        return getByField("id", id)
    }

    override fun getAll(): Deferred<List<Document>> {
        return coroutineScope.async { documents.values.toList() }
    }
}