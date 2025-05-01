package mixin;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.MeCommand;
import net.minecraft.server.command.MessageCommand;
import net.minecraft.server.command.ServerCommandSource;
import org.qbrp.system.utils.log.Logger;
import org.qbrp.system.utils.log.Loggers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;


@Mixin(MessageCommand.class)
public class WhisperCommandMixin {
    @Unique
    private static Logger logger = Loggers.INSTANCE.get("mixin");

    /**
     * @author lain1wakura
     * @reason Ванильные команды сообщения отключены, поскольку некоторые сервера (в частности, работающие на Banner) не обрабатывают замену команды.
     */
    @Overwrite
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        logger.warn("Инициализация /msg, /tell, /w заблокирована!");
    }
}
