package org.qbrp.main.core.fabricmc.player

import java.util.UUID

@JvmInline
value class MinecraftPlayerId(val value: UUID)

fun UUID.toPlayerMc() = MinecraftPlayerId(this)