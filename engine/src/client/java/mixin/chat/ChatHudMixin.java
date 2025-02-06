package mixin.chat;

import com.google.common.collect.Lists;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.util.Formatting;
import org.qbrp.engine.client.EngineClient;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Mixin(ChatHud.class)
public abstract class ChatHudMixin {

    @Mutable
    @Final
    @Shadow
    private List<ChatHudLine.Visible> visibleMessages;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(CallbackInfo ci) {
        this.visibleMessages = EngineClient.Companion.getChatModuleAPIorThrow().getMessages();
    }

//    @Inject(method = "render", at = @At("HEAD"))
//    private void onRender(CallbackInfo ci) {
//        this.visibleMessages = EngineClient.Companion.getChatModuleAPIorThrow().getMessages();
//    }
}