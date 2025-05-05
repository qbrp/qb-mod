package org.qbrp.core.assets.common

import org.qbrp.core.assets.FileSystem

open class Key(val value: String) {
    fun getDir() = "qbrp/assets/${value}"
}