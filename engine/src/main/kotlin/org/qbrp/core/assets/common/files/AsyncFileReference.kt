package org.qbrp.core.assets.common.files

import kotlinx.coroutines.runBlocking
import org.qbrp.core.assets.common.Asset

interface AsyncFileReference<T: Asset>: FileReference<T> {
    suspend fun readAsync(): T
    override fun read(): T {
        return runBlocking { readAsync() }
    }
}