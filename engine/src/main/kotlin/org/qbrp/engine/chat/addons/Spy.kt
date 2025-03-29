package org.qbrp.engine.chat.addons

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
import org.qbrp.core.game.commands.CommandBuilder
import org.qbrp.core.game.commands.Deps
import org.qbrp.core.game.commands.annotations.Command
import org.qbrp.core.game.commands.annotations.Execute
import org.qbrp.core.game.commands.templates.CallbackCommand
import org.qbrp.core.game.registry.CommandsRepository
import org.qbrp.core.game.registry.ServerModCommand
import org.qbrp.engine.Engine
import org.qbrp.engine.chat.ChatAPI
import org.qbrp.engine.chat.ChatAddon
import org.qbrp.engine.chat.ChatModule
import org.qbrp.engine.chat.addons.groups.ChatGroupsAPI
import org.qbrp.engine.chat.core.events.MessageHandledEvent

import org.qbrp.system.modules.Autoload
import org.qbrp.system.modules.LoadPriority
import org.qbrp.system.networking.messages.types.BooleanContent
import org.qbrp.system.networking.messages.types.StringContent
import kotlin.collections.MutableMap


@Autoload(LoadPriority.ADDON)
class Spy(): ChatAddon("spy"), ServerModCommand {
    private val server: MinecraftServer by inject()
    private val chatGroupsAPI = Engine.getAPI<ChatGroupsAPI>()
    private lateinit var ignoreSpyPlayersMap: MutableMap<ServerPlayerEntity, Boolean>

    override fun load() {
        super.load()
        ignoreSpyPlayersMap = get()
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
                .filter { ignoreSpyPlayersMap[it] != true }
                .toMutableList()
            if (!spyPlayers.isEmpty()) {
                val sender = chatAPI!!.createSender().apply {
                    addTargets(spyPlayers)
                }
                val spyMessage = message.copy().apply {
                    setText("&6[S]&r ${getText()}")
                    setTags(getTagsBuilder()
                        .component("spy", StringContent())
                        .component("handleVolume", BooleanContent(false)))
                    handleUpdate()
                }
                sender.send(spyMessage)
            }
            ActionResult.PASS
        }
    }

    override fun getKoinModule(): Module = module {
        single { mutableMapOf<ServerPlayerEntity, Boolean>() }
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
            val spyPlayersMap = get<MutableMap<ServerPlayerEntity, Boolean>>()
            val currentValue = spyPlayersMap.getOrPut(ctx.source.player!!) { false }
            spyPlayersMap[ctx.source.player!!] = !currentValue
            callback(ctx, "Слежка ${if (currentValue) "включена" else "выключена"}")
        }
    }
}