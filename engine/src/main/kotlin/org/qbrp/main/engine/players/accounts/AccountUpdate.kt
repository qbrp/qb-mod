package org.qbrp.main.engine.players.accounts

import org.bson.conversions.Bson

data class AccountUpdate(
    val updates: List<Bson>,
    val arrayFilters: List<Bson> = emptyList()
)