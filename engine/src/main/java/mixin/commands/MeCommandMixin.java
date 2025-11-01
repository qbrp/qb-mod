package mixin.commands;

import com.mojang.brigadier.CommandDispatcher;

import net.minecraft.server.command.MeCommand;
import net.minecraft.server.command.ServerCommandSource;
import org.qbrp.main.core.util.log.Logger;
import org.qbrp.main.core.util.log.LoggerUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;


@Mixin(MeCommand.class)
public class MeCommandMixin {
    @Unique
    private static Logger logger = LoggerUtil.INSTANCE.get("mixin");

    /**
     * @author lain1wakura
     * @reason Ванильный me отключен, поскольку некоторые сервера (в частности, работающие на Banner) не обрабатывают замену команды.
     */
    @Overwrite
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        logger.warn("Инициализация /me заблокирована!");
    }
}
