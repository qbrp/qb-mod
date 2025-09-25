package engine.anticheat

import core.TestEnv
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import net.minecraft.Bootstrap
import net.minecraft.SharedConstants
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.qbrp.main.core.mc.player.dataformats.MinecraftPlayerId
import org.qbrp.main.core.mc.player.dataformats.Username
import org.qbrp.main.core.mc.player.model.PlayerSnapshot
import org.qbrp.main.engine.anticheat.ModListHandler
import org.qbrp.main.engine.anticheat.OpNotificator
import java.util.UUID

class AnticheatTest: TestEnv() {
    private lateinit var player: PlayerSnapshot

    companion object {
        @BeforeAll
        @JvmStatic
        fun boot() {
            SharedConstants.createGameVersion()
            Bootstrap.initialize()
        }
    }

    @BeforeEach
    fun setup() {
        val pUuid = MinecraftPlayerId(UUID.randomUUID())
        player = mockk<PlayerSnapshot>(relaxed = true) {
            every { uuid } returns pUuid
            every { username } returns Username("TestPlayer")
        }
    }

    @Test
    fun operatorNotifiedIfPlayerKicked() {
        val ctx = newMinecraftContext()
        val opNotificator = mockk<OpNotificator>(relaxed = true)
        val modIdHandler = ModListHandler(opNotificator)
        with(ctx) {
            modIdHandler.onModIds(player, listOf("xaeroworldmap"))
            verify { opNotificator.notifyOperators(any()) }
        }
        val playerUuid = player.uuid
        verify { ctx.playerModeration.disconnect(playerUuid, any()) }
    }

    @Test
    fun playerNotKickedIfDontHaveRestrictedMods() {
        val ctx = newMinecraftContext()
        with(ctx) {
            val opNotificator = mockk<OpNotificator>(relaxed = true)
            val modIdHandler = ModListHandler(opNotificator)
            modIdHandler.onModIds(player, listOf(""))
        }
        val playerUuid = player.uuid
        verify(exactly = 0) { ctx.playerModeration.disconnect(playerUuid, any()) }
    }

}