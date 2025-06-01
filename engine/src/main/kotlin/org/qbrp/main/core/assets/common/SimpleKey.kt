package org.qbrp.main.core.assets.common

import java.io.File

class SimpleKey(val path: File): Key {
    override fun getId(): String {
        return path.path
    }
}