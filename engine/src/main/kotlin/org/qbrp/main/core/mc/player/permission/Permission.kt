package org.qbrp.main.core.mc.player.permission

import org.qbrp.main.core.Core

@JvmInline
value class Permission(val name: String) {
    val qualifiedName: String get() = "${Core.MOD_ID}.$name"
    val isEmpty get() = name == ""
}