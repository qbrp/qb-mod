package org.qbrp.client.engine.anticheat

import io.netty.buffer.Unpooled
import net.fabricmc.api.EnvType
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.network.PacketByteBuf
import org.qbrp.client.core.networking.v2.onRequire
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.EngineModule
import org.qbrp.main.engine.anticheat.AnticheatModule
import org.qbrp.main.engine.anticheat.ModIdsPacket

@Autoload(env = EnvType.CLIENT)
class ModIdPacketSenderModule: EngineModule("anticheat") {
    override fun onLoad() {
        onRequire(AnticheatModule.REQUEST_ID) { packetSender ->
            val buf = PacketByteBuf(Unpooled.buffer())
            ModIdsPacket(FabricLoader.getInstance().allMods.map { it.metadata.id }).write(buf)
            packetSender.post(buf)
        }
    }

}