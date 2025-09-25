package org.qbrp.main.engine.anticheat

import org.koin.core.component.get
import org.koin.core.component.inject
import org.koin.core.module.Module
import org.koin.dsl.module
import org.qbrp.main.core.events.on
import org.qbrp.main.core.mc.context.MinecraftContext
import org.qbrp.main.core.mc.player.events.AsyncMinecraftPlayerJoinEvent
import org.qbrp.main.core.mc.player.exists
import org.qbrp.main.core.mc.player.isOp
import org.qbrp.main.core.mc.server.context.withCoreContext
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.EngineModule
import org.qbrp.main.core.networking.require.RequireId
import org.qbrp.main.core.networking.require.awaitRequire
import org.qbrp.main.engine.players.model.service.ServerPlayerStorage

@Autoload(10)
class AnticheatModule: EngineModule("anticheat"), OpNotificator {
    companion object {
        val MINIMAP = """Моды на мини-карты запрещены, поскольку позволяют получать информацию о местонахождении игроков и построек. """ +
                    """Это нарушает принцип мета-информации и невозможно по РП: ваш персонаж в теории не способен знать это.""".trimMargin()
        val REQUEST_ID = RequireId("get_mod_ids")
        val RESTRICTED_MODS = listOf(
            UndesirableMod("freecam", """<red>""" +
                    """Вы используете мод <bold>Freecam</bold></red>.""" + "<newline>" +
                    """<gold>Он крайне не рекомендуется для игры на сервере и нарушает принцип мета-информации. Администрация сервера была уведомлена о его наличии.""" +
                    """</gold>""".trimMargin()),
            RestrictedMod("xaeroworldmap", "Xaero's WorldEnv Map", MINIMAP),
            RestrictedMod("xaerominimap", "Xaero's Minimap", MINIMAP),
            RestrictedMod("voxelmap", "Voxel Map", MINIMAP)
        )
    }

    override fun getKoinModule(): Module = module {
        single<OpNotificator> { this@AnticheatModule }
        single { ModListHandler(get()) }
    }

    override fun onEnable() = withCoreContext {
        on<AsyncMinecraftPlayerJoinEvent>() {
            val (buf, ctx) = it.player.awaitRequire(REQUEST_ID)
            ModIdsPacket(buf, it.player, get()).apply(get())
            if (it.player.exists(get())) it.cancel()
        }
    }

    private val storage: ServerPlayerStorage by inject()
    context(ctx: MinecraftContext)
    override fun notifyOperators(text: String) {
       storage.forEach { if (it.isOp) it.sendChatMessage(text) }
    }
}