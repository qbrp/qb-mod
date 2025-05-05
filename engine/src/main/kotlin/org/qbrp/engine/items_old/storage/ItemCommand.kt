//package org.qbrp.engine.items.storage
//
//import com.mojang.brigadier.CommandDispatcher
//import com.mojang.brigadier.arguments.StringArgumentType
//import com.mojang.brigadier.context.CommandContext
//import net.minecraft.server.command.CommandManager
//import net.minecraft.server.command.ServerCommandSource
//import org.qbrp.engine.items.model.ItemManager
//import org.qbrp.core.mc.registry.ServerModCommand
//import org.qbrp.core.resources.ServerResources
//import org.qbrp.core.resources.content.ItemStorage
//import org.qbrp.system.utils.keys.Key
//
//class ItemCommand(val manager: ItemManager): ServerModCommand {
//    override fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
//        dispatcher.register(
//            CommandManager.literal("itemcreate")
//                .then(
//                    CommandManager.argument("directory", StringArgumentType.word())
//                        .suggests { _, builder ->
//                            ServerResources.getItems().suggestDirectories(builder)
//                        }
//                        .then(
//                            CommandManager.argument("item", StringArgumentType.word())
//                                .suggests { ctx, builder ->
//                                    getDir(ctx).suggestItems(builder)
//                                }
//                                .then(
//                                    CommandManager.argument("tag", StringArgumentType.greedyString())
//                                        .suggests { ctx, builder ->
//                                            getDir(ctx).getItem(getItemArg(ctx)).suggestItemTag(builder)
//                                        }
//                                        .executes { ctx ->
//                                            try {
//                                                val item = getDir(ctx).getItem(getItemArg(ctx))
//                                                manager.wrapItem(
//                                                    item,
//                                                    getTagArg(ctx),
//                                                    ctx.source.player ?: return@executes 0
//                                                )
//                                            } catch (e: Exception) {
//                                                e.printStackTrace()
//                                            }
//                                            1
//                                        }
//                                )
//                        )
//                )
//        )
//    }
//
//
//    private fun getDir(ctx: CommandContext<ServerCommandSource>, name: String = getDirArg(ctx)): ItemStorage = (ServerResources.getItems().registry(Key(name)) as ItemStorage)
//
//    private fun getDirArg(ctx: CommandContext<ServerCommandSource>): String {
//        return StringArgumentType.getString(ctx, "directory")
//    }
//
//    private fun getItemArg(ctx: CommandContext<ServerCommandSource>): String {
//        return StringArgumentType.getString(ctx, "item")
//    }
//
//    private fun getTagArg(ctx: CommandContext<ServerCommandSource>): String {
//        return StringArgumentType.getString(ctx, "tag")
//    }
//}