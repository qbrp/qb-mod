package mixin;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import org.qbrp.core.Core;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Этот класс отвечает за удаление стандартных вкладок инвентаря, изменяя логику их регистрации в целом. Логика создания новых вкладок определяется не здесь.
 * Майнкрафт вылетает, если удалить стандартные вкладки - к ним относятся поиск, сохраненные предметы, сам инвентарь и строительные блоки.
 **/
@Mixin(ItemGroups.class)
public class ItemGroupsMixin {
    @Unique
    private static final Set<String> ALLOWED_TABS = new HashSet<>();
    static {
        ALLOWED_TABS.add("minecraft:search");
        ALLOWED_TABS.add("minecraft:hotbar");
        ALLOWED_TABS.add("minecraft:inventory");
    }

    @Redirect(
            method = "registerAndGetDefault",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/registry/Registry;register(Lnet/minecraft/registry/Registry;Lnet/minecraft/registry/RegistryKey;Ljava/lang/Object;)Ljava/lang/Object;"
            )
    )
    private static <T> T redirectRegister(Registry<T> registry, RegistryKey<T> key, T entry) {
        String keyName = key.getValue().toString();
        if (ALLOWED_TABS.contains(keyName)) {
            return Registry.register(registry, key, entry); }
        return null;
    }

    /**
     * @author lain1wakura
     * @reason При открытии инвентаря будет автоматически открываться 2 страница
     */
    @Overwrite
    public static ItemGroup getDefaultTab() {
        return (ItemGroup) Registries.ITEM_GROUP.getOrThrow(RegistryKey.of(Registries.ITEM_GROUP.getKey(), new Identifier(Core.MOD_ID, "default")));
    }
}