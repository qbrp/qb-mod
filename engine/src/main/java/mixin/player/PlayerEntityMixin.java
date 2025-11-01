package mixin.player;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.qbrp.main.engine.players.NicknameMixinAccess;
import org.qbrp.main.engine.players.model.ServerPlayerObject;
import org.qbrp.main.engine.players.model.service.PlayersUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(PlayerEntity.class)
@Environment(EnvType.SERVER)
public abstract class PlayerEntityMixin extends LivingEntity {

    @Shadow public abstract Text getName();
    @Unique private Text customDisplayName = Text.literal("Загрузка имени...");

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Unique
    private void loadData() {
        String name = this.getName().getString();
        if (name != null && !name.isEmpty()) {
            ServerPlayerObject data = PlayersUtil.INSTANCE.getPlayerSession(name);
            if (data != null) {
                customDisplayName = NicknameMixinAccess.INSTANCE.getPlayerDisplayName(data);
            }
        }
    }

    @ModifyArg(method = "getDisplayName", at = @At(value = "INVOKE", target = "Lnet/minecraft/scoreboard/Team;decorateName(Lnet/minecraft/scoreboard/AbstractTeam;Lnet/minecraft/text/Text;)Lnet/minecraft/text/MutableText;"))
    private Text replaceName(Text text) {
        loadData();
        return customDisplayName;
    }

}