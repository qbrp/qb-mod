package org.qbrp.main.core.assets

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.qbrp.main.core.assets.common.Asset
import org.qbrp.main.core.assets.common.AssetLifecycleManager
import org.qbrp.main.core.assets.common.AssetsStorage
import org.qbrp.main.core.assets.common.Key
import org.qbrp.main.core.assets.common.references.AsyncFileReference
import org.qbrp.main.core.assets.common.references.FileReference
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.LoadPriority
import org.qbrp.main.core.modules.QbModule
import java.io.FileNotFoundException

@Autoload(LoadPriority.HIGHEST, both = true)
class Assets: QbModule("assets"), AssetsAPI {
    private lateinit var storage: AssetsStorage
    private val defaultScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onLoad() {
        storage = getLocal()
    }

    override fun getKoinModule() = inner<AssetsAPI>(this) {
        scoped { AssetsStorage() }
        scoped { AssetLifecycleManager(get()) }
    }

    override fun register(key: Key, asset: Asset) {
        storage.addAsset(key.getId(), asset)
    }

    override fun register(key: String, asset: Asset) {
        storage.addAsset(key, asset)
    }

    override fun <T : Asset> load(fileReference: FileReference<T>): T {
        if (!fileReference.exists()) throw FileNotFoundException("Файл ${fileReference.key.getId()} не существует")
        return fileReference.read().also {
            register(fileReference.getKey(), it)
        }
    }

    override fun <T : Asset> loadOrCreate(asset: T, fileReference: FileReference<T>): T {
        if (!fileReference.exists()) fileReference.write(asset)
        return fileReference.read().also {
            register(fileReference.getKey(), it)
        }
    }

    override fun <T : Asset> getOrCreate(asset: T, fileReference: FileReference<T>): T {
        return getByKey(fileReference.key) ?: loadOrCreate(asset, fileReference)
    }

    override fun <T : Asset> getOrLoad(fileReference: FileReference<T>): T {
        getByKey<T>(fileReference.key)?.let {
            return it
        }
        if (!fileReference.exists()) throw FileNotFoundException("Файл ${fileReference.key.getId()} не существует")
        return fileReference.read().also {
            register(fileReference.getKey(), it)
        }
    }

    override fun <T : Asset> getByKey(key: Key): T? {
        return storage.getAsset<T>(key.getId())
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun <T : Asset> loadAsync(
        ref: FileReference<T>,
        onComplete: (T) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        storage.getAsset<T>(ref.key.getId())?.let {
            onComplete(it)
            return
        }
        defaultScope.launch(Dispatchers.IO) {
            try {
                if (!ref.exists()) throw FileNotFoundException("Файл ${ref.key.getId()} не существует")
                val asset = ref.read()
                storage.addAsset(ref.key.getId(), asset)
                onComplete(asset)
            } catch (t: Throwable) {
                onError(t)
            }
        }
    }

    override fun <T : Asset> getOrLoadAsync(
        ref: FileReference<T>,
        onComplete: (T) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        storage.getAsset<T>(ref.getKey())?.let(onComplete) ?: loadAsync(ref, onComplete, onError)
    }

    override suspend fun <T : Asset> loadSuspend(ref: AsyncFileReference<T>): T = withContext(Dispatchers.IO) {
        return@withContext getOrLoad(ref)
    }

    override suspend fun <T : Asset> getOrLoadSuspend(ref: AsyncFileReference<T>) = withContext(Dispatchers.IO) {
        return@withContext storage.getAsset<T>(ref.getKey()) ?: getOrLoad(ref)
    }
}