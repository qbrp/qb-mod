package org.qbrp.main.core.versions

enum class CompatibleState {
    COMPATIBLE,
    PARTIALLY_COMPATIBLE, // Версия не та, но играть можно
    INCOMPATIBLE
}