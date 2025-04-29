package org.qbrp.engine.chat.addons.spy

import PermissionManager.hasPermission
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import net.minecraft.server.MinecraftServer
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.ActionResult
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.component.inject
import org.koin.core.module.Module
import org.koin.dsl.module
import org.qbrp.core.mc.commands.CommandBuilder
import org.qbrp.core.mc.commands.Deps
import org.qbrp.core.mc.commands.annotations.Command
import org.qbrp.core.mc.commands.annotations.Execute
import org.qbrp.core.mc.commands.templates.CallbackCommand
import org.qbrp.core.mc.registry.CommandsRepository
import org.qbrp.core.mc.registry.ServerModCommand
import org.qbrp.engine.Engine
import org.qbrp.engine.chat.ChatAddon
import org.qbrp.engine.chat.ChatModule
import org.qbrp.engine.chat.addons.groups.ChatGroupsAPI
import org.qbrp.engine.chat.core.events.MessageHandledEvent

import org.qbrp.system.modules.Autoload
import org.qbrp.system.modules.LoadPriority
import org.qbrp.system.networking.messages.types.StringContent


@Autoload(LoadPriority.ADDON)
class Spy(): ChatAddon("spy"), ServerModCommand {
    private val server: MinecraftServer by inject()
    private val chatGroupsAPI = Engine.getAPI<ChatGroupsAPI>()
    private lateinit var spyManager: SpyManager

    override fun load() {
        super.load()
        spyManager = get()
        CommandsRepository.add(this)

        MessageHandledEvent.EVENT.register { message, receivers ->
            if (message.authorName == ChatModule.SYSTEM_MESSAGE_AUTHOR) return@register ActionResult.PASS
            if (message.getTags().isComponentExists("spy") == true) return@register ActionResult.PASS
            if (message.getAuthorEntity() == null) return@register ActionResult.PASS
            val spyPlayers = getSpyPlayers()
                .filterNot { it.name.string == message.authorName }
                .filterNot { it in receivers }
                .filter { chatGroupsAPI!!.fetchGroup(message)?.playerHasReadPermission(it) == true }
                .filter { it.hasPermission("chat.spy") }
                .filter { spyManager.playerCanSpy(it) }
                .toMutableList()
            if (!spyPlayers.isEmpty()) {
                val sender = chatAPI.createSender().apply {
                    addTargets(spyPlayers)
                }
                val spyMessage = message.copy().apply {
                    setText("&6[S]&r ${getText().replace("{radar}", "")}")
                    setTags(getTagsBuilder()
                        .component("sound", "")
                        .component("spy", StringContent())
                        .component("handleVolume", false)
                        .component("spectators", false)
                        .component("radar", false))
                    handleUpdate()
                }
                sender.send(spyMessage)
            }
            ActionResult.PASS
        }
    }

    override fun getKoinModule(): Module = module {
        single { SpyManager() }
    }

    fun getSpyPlayers(): List<ServerPlayerEntity> {
        return server.playerManager.playerList
    }

    override fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
        dispatcher.register(
            CommandBuilder()
                .buildTree(SpyCommand::class.java)
                .getCommand()
                .getLiteral()
        )
    }

    @Command("qbspy")
    class SpyCommand(): CallbackCommand(), KoinComponent {
        @Execute(permission = "chat.spy")
        fun execute(ctx: CommandContext<ServerCommandSource>, deps: Deps) {
            try {
                val spyManager = get<SpyManager>()
                val player = ctx.source.player!!
                spyManager.toggleIgnoreSpy(player)
                callback(ctx, "Слежка ${if (spyManager.playerCanSpy(player)) "включена" else "выключена"}")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}