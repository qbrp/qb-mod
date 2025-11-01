package mixin.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.command.TeleportCommand;
import org.qbrp.main.core.util.log.Logger;
import org.qbrp.main.core.util.log.LoggerUtil;
import org.qbrp.main.engine.physics.player.EngineTeleportCommandsMixinAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;


@Mixin(TeleportCommand.class)
public class TeleportCommandMixin {
    @Unique
    private static Logger logger = LoggerUtil.INSTANCE.get("mixin");

    /**
     * @author lain1wakura
     * @reason Ванильный tp отключен, так как несовместима с новым физическим движком, с помощью которого
     * обрабатывается движение. Альтернатива - /to, /teleport, /move
     */
    @Overwrite
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        logger.warn("Инициализация /tp заблокирована!");
        EngineTeleportCommandsMixinAccess.INSTANCE.registerTeleportCommand(dispatcher);
    }
}
