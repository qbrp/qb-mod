package org.qbrp.main.core.game.storage

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import net.minecraft.server.MinecraftServer
import org.koin.core.context.GlobalContext
import org.qbrp.main.core.game.saving.Saver
import org.qbrp.main.core.components.serialization.ContextualSerializer
import org.qbrp.main.core.components.serialization.Identifiable
import org.qbrp.main.core.dataformats.StringId
import org.qbrp.main.core.storage.TableAccess
import java.util.concurrent.ConcurrentHashMap

open class TableRepository<T : Identifiable, C>(
    private val table: TableAccess,
    private val factory: ContextualSerializer<T, C>,
    open val cache: Storage<T>
): Saver<T> {
    protected val scope by lazy { CoroutineScope(Dispatchers.IO) }
    private val loadingMap = ConcurrentHashMap<StringId, Deferred<T?>>()

    fun getByIdOrLoad(
        id: StringId,
        ctx: C,
        onLoad: (T) -> Unit,
        notFound: () -> Unit = {}
    ) {
        fun runOnServerThread(r: Runnable) {
            GlobalContext.get().get<MinecraftServer>().execute { r.run() }
        }
        val deferred: Deferred<T?> = loadingMap.computeIfAbsent(id) {
            scope.async {
                cache.getById(id.value)?.let { cached ->
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
