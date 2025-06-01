package org.qbrp.main.core.assets

import org.qbrp.main.core.assets.common.Asset
import org.qbrp.main.core.assets.common.references.AsyncFileReference
import org.qbrp.main.core.assets.common.references.FileReference

interface AsyncAssetsAPI {
    fun <T : Asset> loadAsync(
        ref: FileReference<T>,
        onComplete: (T) -> Unit,
        onError: (Throwable) -> Unit = { throw it },
    )
    fun <T : Asset> getOrLoadAsync(
        ref: FileReference<T>,
        onComplete: (T) -> Unit,
        onError: (Throwable) -> Unit = { throw it },
    )
    suspend fun <T : Asset> loadSuspend(
        ref: AsyncFileReference<T>,
    ): T
    suspend fun <T : Asset> getOrLoadSuspend(
        ref: AsyncFileReference<T>,
    ): T
}