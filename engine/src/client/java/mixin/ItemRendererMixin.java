package mixin;

import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.client.MinecraftClient;
import org.jetbrains.annotations.Nullable;
import org.qbrp.engine.client.ModelRepo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {
    private static final ModelIdentifier MY_MODEL_ID =
            new ModelIdentifier(new Identifier("qbrp", "abstract_generated"), "inventory");

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
        BakedModel original = cir.getReturnValue();
        if (!stack.hasNbt()) return;

        var nbt = stack.getNbt();
        assert nbt != null;
        if (!nbt.contains("QbrpModel", 8)) return;

        String tag = nbt.getString("QbrpModel");            // e.g. "tactical_tomahawk#inventory"
        String[] parts = tag.split("#", 2);
        String modelName = parts[0];                        // "tactical_tomahawk"
        String variant   = parts.length > 1 ? parts[1] : "inventory";

        // 1) namespace:path — если нет ":", то дописываем "qbrp:"
        Identifier baseId = modelName.contains(":")
                ? Identifier.tryParse(modelName)
                : new Identifier("qbrp", modelName);

        // 2) путь к файлу — Minecraft сам ищет по resourcePath "models/<baseId.path>.json"
        //    но для предметов чаще всего это assets/.../models/item/<name>.json
        //    поэтому, если путь не содержит "/", допишем подпапку "item/"
        Identifier fullId = baseId.getPath().contains("/")
                ? baseId
                : new Identifier(baseId.getNamespace(), "models/item/" + baseId.getPath());

        ModelIdentifier modelId = new ModelIdentifier(fullId, variant);

        // Получаем уже запечённую модель
        BakedModelManager mgr = MinecraftClient.getInstance().getBakedModelManager();
        BakedModel custom = mgr.getModel(modelId);
        cir.setReturnValue(custom);
        return;

//        if (custom != null) {
//            cir.setReturnValue(custom);
//        }
    }
}
