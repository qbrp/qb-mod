package org.qbrp.main.core.assets.common.references

import kotlinx.coroutines.runBlocking
import org.qbrp.main.core.assets.common.Asset

interface AsyncFileReference<T: Asset>: FileReference<T> {
    suspend fun readAsync(): T
    override fun read(): T {
        return runBlocking { readAsync() }
    }
}