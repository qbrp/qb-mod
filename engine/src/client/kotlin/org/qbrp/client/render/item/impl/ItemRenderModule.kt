package org.qbrp.client.render.item.impl

import net.fabricmc.api.EnvType
import net.minecraft.client.MinecraftClient
import org.koin.core.module.Module
import org.qbrp.client.render.item.impl.mc.ItemStackAdapterWrapper
import org.qbrp.client.render.item.impl.render.ItemRendererImpl
import org.qbrp.client.render.item.model.render.ItemRenderer
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.EngineModule

@Autoload(env = EnvType.CLIENT)
class ItemRenderModule: EngineModule("item-render") {
    override fun getKoinModule(): Module = innerWithApi({
        single { ItemStackAdapterWrapper() }
        single<ItemRenderer> { ItemRendererImpl(get(), MinecraftClient.getInstance().itemRenderer) }
    }, {
    })
}