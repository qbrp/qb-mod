package org.qbrp.main.core.components

data class Name(val full: String) {
    val customName: String = full.substringAfter("#", full.substringBefore("#"))
    val type = full.substringBefore("#", full.substringAfter("#"))
}