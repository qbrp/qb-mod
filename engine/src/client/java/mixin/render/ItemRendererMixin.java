package mixin.render;

import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.client.MinecraftClient;
import org.jetbrains.annotations.Nullable;
import org.qbrp.client.ClientCore;
import org.qbrp.client.core.resources.ModelRepository;
import org.qbrp.client.render.item.impl.mc.ItemStackAdapterWrapper;
import org.qbrp.main.core.modules.EngineModule;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {
    @Inject(
            method = "getModel(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;Lnet/minecraft/entity/LivingEntity;I)Lnet/minecraft/client/render/model/BakedModel;",
            at = @At("RETURN"),
            cancellable = true
    )
    private void onGetModel(
            ItemStack stack,
            @Nullable World world,
            @Nullable LivingEntity entity,
            int seed,
            CallbackInfoReturnable<BakedModel> cir
    ) {
        if (!stack.hasNbt()) return;

        var nbt = stack.getNbt();
        assert nbt != null;
        if (!nbt.contains("QbrpModel", 8)) return;

        String tag = nbt.getString(ItemStackAdapterWrapper.MODEL_TAG);
        ModelRepository modelRepo = (ModelRepository) ((EngineModule) Objects.requireNonNull(ClientCore.INSTANCE.getModule("model-loader"))).getAPI();
        assert modelRepo != null;

        ModelIdentifier id = modelRepo.getResourceLocation(tag);

        BakedModelManager mgr = MinecraftClient.getInstance().getBakedModelManager();
        BakedModel custom = mgr.getModel(id);
        cir.setReturnValue(custom);
    }
}
