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
import org.qbrp.client.ModelRepo;
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

    @Unique
    private static final ModelIdentifier MY_MODEL_ID =
            new ModelIdentifier(new Identifier("qbrp", "abstract_generated"), "inventory");

    @Unique
    private static final ModelIdentifier TEST_TOMAHAWK =
            new ModelIdentifier(new Identifier("qbrp", "tactical_tomahawk"), "gui");

    @Inject(
            method = "<init>",
            at = @At("TAIL")
    )
    private void mixinInit(
            BlockColors blockColors, Profiler profiler, Map<Identifier, JsonUnbakedModel> jsonUnbakedModels, Map blockStates, CallbackInfo ci
    ) throws FileNotFoundException {
        ResourceManager resourceManager = MinecraftClient.getInstance().getResourceManager();
        //Resource resource = resourceManager.getResourceOrThrow(new Identifier("qbrp", "tactical_tomahawk"));
        //jsonUnbakedModels.put(resource);
        //addModel(TEST_TOMAHAWK);
        Map<Identifier, Resource> allModels = ModelLoader.MODELS_FINDER.findResources(resourceManager);


        Map<Identifier, JsonUnbakedModel> qbrpModels =
                jsonUnbakedModels.entrySet().stream()
                        .filter(entry -> "qbrp".equals(entry.getKey().getNamespace()))
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                Map.Entry::getValue
                        ));

        qbrpModels.forEach((id, res) -> {
            String fixedPath = id.getPath().replace(".json", "").replace("models/item/", "");
            Identifier fixedId = new Identifier("qbrp", fixedPath);
            ModelIdentifier modelId = new ModelIdentifier(fixedId, "inventory");
            System.out.println("Found model: " + modelId);
            this.addModel(new ModelIdentifier(modelId, "inventory"));
            if (Objects.equals(id.getPath(), "models/item/tactical-tomahawk.json")) {
                ModelRepo.INSTANCE.setMODEL(new ModelIdentifier(modelId, "inventory"));
            }
        });
    }
}
