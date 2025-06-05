package org.qbrp.main.core.game.storage

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import net.minecraft.item.ItemStack
import org.qbrp.main.core.Core
import org.qbrp.main.core.game.saving.Saver
import org.qbrp.main.core.game.serialization.ContextualFactory
import org.qbrp.main.core.game.serialization.Identifiable
import org.qbrp.main.core.storage.TableAccess
import org.qbrp.main.engine.items.model.ServerItemObject

open class TableRepository<T : Identifiable, C>(
    private val table: TableAccess,
    private val factory: ContextualFactory<T, C>,
    private val cache: Storage<T>
): Saver<T> {
    protected val scope by lazy { CoroutineScope(Dispatchers.IO) }
    suspend fun getByIdOrLoad(id: String, ctx: C): T? {
        cache.getById(id)?.let { return it }
        val json = table.getById(id).await()?.toJson() ?: return null
        val obj = factory.fromJson(json, ctx)
        return obj
    }

    fun getByIdOrLoad(
        id: String,
        ctx: C,
        onLoad: (T) -> Unit,
        notFound: () -> Unit = {}
    ) {
        fun runOnThread(r: Runnable) = Core.server.execute { r.run() }
        scope.launch {
            cache.getById(id)?.let { cached ->
                runOnThread { onLoad(cached) }
                return@launch
            }

            val document = table.getById(id).await()
            if (document == null) {
                runOnThread { notFound() }
                return@launch
            }
            val json = document.toJson()
            val obj = factory.fromJson(json, ctx)
            cache.add(obj)
            runOnThread { onLoad(obj) }
        }
    }

    override fun saveObject(obj: T) = table.saveObject(obj.id, factory.toJson(obj))
}
