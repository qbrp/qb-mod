package org.qbrp.main.core.mc.player.service

import org.bson.conversions.Bson

data class AccountUpdate(
    val updates: List<Bson>,
    val arrayFilters: List<Bson> = emptyList()
)