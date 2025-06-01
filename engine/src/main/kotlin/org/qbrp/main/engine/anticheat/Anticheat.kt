package org.qbrp.main.engine.anticheat

import net.minecraft.server.MinecraftServer
import org.koin.core.component.get
import org.koin.core.context.GlobalContext
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.QbModule
import org.qbrp.main.core.utils.networking.messages.Messages
import org.qbrp.main.core.utils.networking.messaging.ServerReceiver
import org.qbrp.main.core.utils.networking.messaging.ServerReceiverContext
import org.qbrp.main.core.utils.format.Format.asMiniMessage

@Autoload(10)
class Anticheat: QbModule("anticheat") {
    val minimap =
        """Моды на мини-карты запрещены, поскольку позволяют получать информацию о местонахождении игроков и построек. """ +
        """Это нарушает принцип мета-информации и невозможно по РП: ваш персонаж в теории не способен знать это.""".trimMargin()

    private val mods = listOf<Mod>(
        UndesirableMod("freecam", """<red>""" +
            """Вы используете мод <bold>Freecam</bold></red>.""" + "<newline>" +
            """<gold>Он крайне не рекомендуется для игры на сервере и нарушает принцип мета-информации. Администрация сервера была уведомлена о его наличии.""" +
        """</gold>""".trimMargin()),
        RestrictedMod("xaeroworldmap", "Xaero's World Map", minimap),
        RestrictedMod("xaerominimap", "Xaero's Minimap", minimap),
        RestrictedMod("voxelmap", "Voxel Map", minimap)
    )

    override fun onLoad() {
        ServerReceiver<ServerReceiverContext>(Messages.MOD_IDS, ModIdListContent::class, { message, context, receiver ->
            if (!context.player.hasPermissionLevel(4)) {
                val modList = message.getContent<List<String>>()
                val restrictedMods = mutableListOf<String>()
                println(modList)
                mods.forEach {
                    if (modList.contains<String>(it.id)) {
                        it.ifFounded(context.player)
                        restrictedMods.add(it.id)
                    }
                }
                if (restrictedMods.isNotEmpty()) {
                    sendToOps("<red>[!]<reset> <gold>${context.player.name.string} зашел на сервер с ${restrictedMods.joinToString(", ")}</gold>")
                }
            }
            true
        }).register()
    }

    companion object {
        fun sendToOps(text: String) {
            GlobalContext.get().get<MinecraftServer>().playerManager.playerList.forEach { player ->
                if (player.hasPermissionLevel(4)) {
                    player.sendMessage(
                        text.asMiniMessage()
                    )
                }
            }
        }
    }
}