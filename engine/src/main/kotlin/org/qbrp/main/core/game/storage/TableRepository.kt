package org.qbrp.main.core.game.storage

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import net.minecraft.item.ItemStack
import org.qbrp.main.core.Core
import org.qbrp.main.core.game.saving.Saver
import org.qbrp.main.core.game.serialization.ContextualFactory
import org.qbrp.main.core.game.serialization.Identifiable
import org.qbrp.main.core.storage.TableAccess
import org.qbrp.main.engine.items.model.ServerItemObject
import java.util.concurrent.ConcurrentHashMap

open class TableRepository<T : Identifiable, C>(
    private val table: TableAccess,
    private val factory: ContextualFactory<T, C>,
    private val cache: Storage<T>
): Saver<T> {
    protected val scope by lazy { CoroutineScope(Dispatchers.IO) }
    private val loadingMap = ConcurrentHashMap<String, Deferred<T?>>()

    fun getByIdOrLoad(
        id: String,
        ctx: C,
        onLoad: (T) -> Unit,
        notFound: () -> Unit = {}
    ) {
        fun runOnServerThread(r: Runnable) {
            Core.server.execute { r.run() }
        }
        val deferred: Deferred<T?> = loadingMap.computeIfAbsent(id) {
            scope.async {
                cache.getById(id)?.let { cached ->
                    return@async cached
                }
                val document = table.getById(id).await()
                if (document == null) {
                    return@async null
                }
                val newObj: T = factory.fromJson(document.toJson(), ctx)
                val existing: T? = cache.putIfAbsent(newObj)
                val finalObj = existing ?: newObj

                return@async finalObj
            }
        }

        scope.launch {
            val result: T? = try {
                deferred.await()
            } finally {
                loadingMap.remove(id)
            }

            if (result == null) {
                runOnServerThread { notFound() }
            } else {
                runOnServerThread { onLoad(result) }
            }
        }
    }

    override fun saveObject(obj: T) = table.saveObject(obj.id, factory.toJson(obj))
}
