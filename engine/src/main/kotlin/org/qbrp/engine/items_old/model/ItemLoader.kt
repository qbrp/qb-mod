//package org.qbrp.engine.items_old.model
//
//import com.mongodb.client.model.Filters
//import com.mongodb.client.model.Sorts
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.SupervisorJob
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.withContext
//import org.koin.core.component.KoinComponent
//import org.litote.kmongo.findOne
//import org.litote.kmongo.getCollection
//import org.litote.kmongo.insertOne
//import org.qbrp.core.resources.ServerResources
//import org.qbrp.system.database.DatabaseService
//
//class ItemLoader: DatabaseService(ServerResources.getConfig().databases.nodeUri, ServerResources.getConfig().databases.items), KoinComponent {
//    init {
//        connect()
//    }
//    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
//
//    // говнокод
//    suspend fun getLastId(): Int = withContext(Dispatchers.IO) {
//        return@withContext db?.getCollection("data")!!
//            .find()
//            .sort(Sorts.descending("id"))
//            .limit(1).first()
//            ?.getInteger("id") ?: 1
//    }
//
//    suspend fun getItemState(id: Int): ItemState? = withContext(Dispatchers.IO) {
//        val collection = db?.getCollection<ItemState>("data")
//        val item = collection?.findOne(Filters.eq("id", id))
//        return@withContext item
//    }
//
//    fun createItem(data: ItemState) {
//        scope.launch {
//            db?.getCollection("data")?.insertOne(data.toJson())
//        }
//    }
//}