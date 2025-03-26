package mixin;

import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import org.qbrp.system.networking.ServerInformation;
import org.qbrp.system.networking.ServerInformationComposer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.Objects;

@Mixin(ChatMessageC2SPacket.class)
public class ChatMessageC2SPacketMixin {

    // Изменяем максимальную длину сообщения при чтении из PacketByteBuf
    @ModifyArg(
            method = "<init>(Lnet/minecraft/network/PacketByteBuf;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/PacketByteBuf;readString(I)Ljava/lang/String;"
            ),
            index = 0
    )
    private static int modifyMaxMessageLengthRead(int original) {
        return Objects.requireNonNullElse((Integer) Objects.requireNonNull(ServerInformation.INSTANCE.getVIEWER()).getComponentData("engine.maxMessageLength"), original);
    }

    @ModifyArg(
            method = "write",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/PacketByteBuf;writeString(Ljava/lang/String;I)Lnet/minecraft/network/PacketByteBuf;"
            ),
            index = 1
    )
    private int modifyMaxMessageLengthWrite(int original) {
        return Objects.requireNonNullElse((Integer) Objects.requireNonNull(ServerInformation.INSTANCE.getVIEWER()).getComponentData("engine.maxMessageLength"), original);
    }
}
