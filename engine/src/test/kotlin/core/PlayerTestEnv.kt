package core

import core.players.service.PlayerSnapshotServiceMock
import core.server.PlayerManagerMock
import core.server.ServerThreadMock
import net.minecraft.Bootstrap
import net.minecraft.SharedConstants
import net.minecraft.server.network.ServerPlayerEntity
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
//import org.junit.jupiter.api.BeforeAll
//import org.junit.jupiter.api.BeforeEach
import org.qbrp.main.core.mc.player.service.attribute.MinecraftPlayerAttributeServiceImpl
import org.qbrp.main.core.mc.player.service.snapshot.MinecraftPlayerSnapshotService
import org.qbrp.main.core.networking.sender.ServerPacketSender
import org.qbrp.main.engine.characters.model.AppearanceData
import org.qbrp.main.engine.players.model.service.PlayerFactory
import org.qbrp.main.engine.players.model.service.PlayerSaver
import org.qbrp.main.engine.players.model.service.PlayerSerializer
import org.qbrp.main.engine.players.model.service.ServerPlayerStorage
import org.qbrp.main.engine.players.characters.entity.CharacterData

abstract class PlayerTestEnv: TestEnv() {
    protected lateinit var connectionHandler: PlayerConnectionHandler
    protected lateinit var table: TableMock
    protected lateinit var world: MainWorldMock
    protected lateinit var storage: ServerPlayerStorage
    protected lateinit var packetSender: ServerPacketSender
    protected lateinit var serializer: PlayerSerializer
    protected lateinit var snapshotService: MinecraftPlayerSnapshotService
    protected lateinit var playerManager: PlayerManagerMock
    protected lateinit var attributesService: MinecraftPlayerAttributeServiceImpl

    companion object {
        @BeforeAll
        @JvmStatic
        fun bootstrap() {
            SharedConstants.createGameVersion();
            Bootstrap.initialize();
        }

        val TEST_CHARACTER = CharacterData("Test character",
            appearance = AppearanceData(
                "Test description",
                mutableListOf(AppearanceData.Look("Test look", null, null)),
                "")
        )
    }

    @BeforeEach
    open fun setup() {
        val serverThread = ServerThreadMock()
        playerManager = PlayerManagerMock()
        attributesService = MinecraftPlayerAttributeServiceImpl(serverThread)
        snapshotService = PlayerSnapshotServiceMock(playerManager, attributesService)
        packetSender = ServerPacketSenderMock()
        table = TableMock()
        world = MainWorldMock()
        storage = ServerPlayerStorage()
        serializer = PlayerSerializer()
        connectionHandler = PlayerConnectionHandler(
            table,
            PlayerFactory(world, snapshotService),
            serializer,
            storage,
            ServerPlayerNetworkHandler(storage, PlayerListMapper(), packetSender),
            PlayerSaver(serializer, table),
            serverThread,
        )
    }

    protected fun addPlayerToStorage(player: ServerPlayerEntity) {
        playerManager.playersMap += player
    }
}