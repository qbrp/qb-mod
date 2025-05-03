package org.qbrp.core.mc.player.model

import org.bson.conversions.Bson

data class AccountUpdate(
    val updates: List<Bson>,
    val arrayFilters: List<Bson> = emptyList()
)