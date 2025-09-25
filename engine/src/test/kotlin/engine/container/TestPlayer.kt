package engine.container

import org.qbrp.main.core.game.IDGenerator
import org.qbrp.main.core.mc.player.dataformats.MinecraftPlayerId
import org.qbrp.main.core.mc.player.dataformats.Username
import org.qbrp.main.engine.players.accounts.Account
import org.qbrp.main.engine.players.auth.SessionData
import org.qbrp.main.engine.players.model.PlayerStateImpl
import org.qbrp.main.engine.players.model.ServerPlayerObject
import org.qbrp.main.engine.players.model.context.PlayerContext
import org.qbrp.main.engine.players.model.util.PlayerId
import java.util.UUID

class TestPlayer(context: PlayerContext, minecraftPlayerId: MinecraftPlayerId, id: PlayerId = PlayerId(IDGenerator.nextIdStr())): ServerPlayerObject(
    context,
    minecraftPlayerId,
    USERNAME,
    Account("test", id.value),
    SessionData(UUID.randomUUID(), id),
    state = PlayerStateImpl()
) {
    companion object {
        val USERNAME = Username("test_player")
    }
}