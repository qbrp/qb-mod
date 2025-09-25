package core.players

import core.PlayerTestEnv
import core.server.ServerThreadMock
import io.kotest.matchers.shouldBe
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.async
import kotlinx.coroutines.test.runTest
import net.minecraft.entity.attribute.EntityAttributeInstance
import net.minecraft.entity.attribute.EntityAttributeModifier
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.math.Vec3d
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.qbrp.main.core.mc.player.service.attribute.MinecraftPlayerAttributeServiceImpl
import org.qbrp.main.core.mc.player.dataformats.MinecraftPlayerId
import org.qbrp.main.core.mc.player.dataformats.Username
import org.qbrp.main.engine.world.location.Position
import org.qbrp.main.engine.world.location.WorldPosition
import java.util.UUID

class McUnitPlayerTests: PlayerTestEnv() {
    private lateinit var playerEntity: ServerPlayerEntity

    @BeforeEach
    fun onSetup() {
        playerEntity = mockk() {
            every { name.string } returns "test_player"
            every { pos } returns Vec3d(12.0, 12.0, 12.0)
            every { uuid } returns UUID.fromString("762e113f-13bc-419a-9a7b-93088b871d60")
        }
    }

    @Test
    fun `factory snapshosts player entity and values is valid`() {
        addPlayerToStorage(playerEntity)
        val snapshot = snapshotService.lazySnapshotPlayer(MinecraftPlayerId(playerEntity.uuid))
        Username("test_player") shouldBe snapshot.username
        WorldPosition(12.0, 12.0, 12.0) as Position shouldBe snapshot.pos
        MinecraftPlayerId(UUID.fromString("762e113f-13bc-419a-9a7b-93088b871d60")) shouldBe snapshot.uuid
    }

    @Test
    fun `getSpeedModifier returns correct SpeedModifier`() {
        val player = mockk<ServerPlayerEntity>()
        val attrInstance = mockk<EntityAttributeInstance>(relaxed = true)
        val uuid = UUID.randomUUID()

        every { player.uuid } returns uuid
        every { player.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED) } returns attrInstance

        val modifierId = UUID.randomUUID()
        val modifier = mockk<EntityAttributeModifier>(relaxed = true)
        every { attrInstance.getModifier(modifierId) } returns modifier

        every { modifier.id } returns modifierId
        every { modifier.value } returns 0.5

        val service = MinecraftPlayerAttributeServiceImpl(ServerThreadMock())
        service.instantiateDefaultAttributeStates(player, 0.5)
        // При первом вызове getOrCreateState создаётся состояние и добавляется модификатор
        every { attrInstance.addPersistentModifier(any()) } just Runs
        service.instantiateDefaultAttributeStates(player, 0.5)

        val speedModifier = service.getSpeedModifier(player)

        0.5 shouldBe speedModifier.value
    }

    @Test
    fun getSpeedModifierIsThreadSafe() = runTest {
        val player = mockk<ServerPlayerEntity>()
        val attrInstance = mockk<EntityAttributeInstance>()
        val uuid = UUID.randomUUID()

        every { player.uuid } returns uuid
        every { player.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED) } returns attrInstance

        val modifierId = UUID.randomUUID()
        val modifier = mockk<EntityAttributeModifier>()

        every { attrInstance.getModifier(any()) } returns modifier
        every { modifier.id } returns modifierId
        every { modifier.value } returns 0.5

        every { attrInstance.addPersistentModifier(any()) } just Runs
        val service = MinecraftPlayerAttributeServiceImpl(ServerThreadMock())
        service.instantiateDefaultAttributeStates(player, 0.5)

        val asyncTask1 = async { service.getSpeedModifier(player).value }
        val asyncTask2 = async { service.getSpeedModifier(player).value }
        val speedModifier1: Double = asyncTask1.await()
        val speedModifier2: Double = asyncTask2.await()
        speedModifier1 shouldBe 0.5
        speedModifier2 shouldBe 0.5
    }
}