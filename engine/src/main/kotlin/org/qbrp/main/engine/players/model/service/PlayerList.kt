package org.qbrp.main.engine.players.model.service

import org.qbrp.main.core.fabricmc.player.PlayerReference
import org.qbrp.main.engine.players.model.PlayerObject
import org.qbrp.main.engine.players.model.ServerPlayerObject

interface GenericPlayerList<P : PlayerObject> : Iterable<P>

typealias PlayerList = GenericPlayerList<out PlayerObject>

interface ServerPlayerList : GenericPlayerList<ServerPlayerObject>

interface PlayerReferenceList : Iterable<PlayerReference>