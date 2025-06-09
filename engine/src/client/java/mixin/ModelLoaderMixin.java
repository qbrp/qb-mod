package mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.qbrp.client.ClientCore;
import org.qbrp.client.ModelRepo;
import org.qbrp.client.core.resources.IdUtil;
import org.qbrp.client.core.resources.ModelRepository;
import org.qbrp.main.core.modules.QbModule;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.FileNotFoundException;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Mixin(ModelLoader.class)
public abstract class ModelLoaderMixin {
    @Shadow protected abstract void addModel(ModelIdentifier modelId);

    @Inject(
            method = "<init>",
            at = @At("TAIL")
    )
    private void mixinInit(
            BlockColors blockColors, Profiler profiler, Map<Identifier, JsonUnbakedModel> jsonUnbakedModels, Map blockStates, CallbackInfo ci
    ) throws FileNotFoundException {
        Map<Identifier, JsonUnbakedModel> modelsList =
                jsonUnbakedModels.entrySet().stream()
                        .filter(entry -> {
                            String namespace = entry.getKey().getNamespace();
                            return "qbrp".equals(namespace) || "tools".equals(namespace);
                        })
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                Map.Entry::getValue
                        ));
        ModelRepository modelRepo = (ModelRepository) ((QbModule)Objects.requireNonNull(ClientCore.INSTANCE.getModule("model-loader"))).getAPI();
        assert modelRepo != null;

        modelsList.forEach((id, res) -> {
            ModelIdentifier fixedId = IdUtil.INSTANCE.clean(id);
            System.out.println("Found model: " + id);
            this.addModel(fixedId);
        });
    }
}
