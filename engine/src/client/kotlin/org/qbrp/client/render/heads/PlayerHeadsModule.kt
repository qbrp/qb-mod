package org.qbrp.client.render.heads

import com.mojang.blaze3d.systems.RenderSystem
import net.fabricmc.api.EnvType
import net.minecraft.util.Identifier
import org.koin.core.component.inject
import org.koin.core.module.Module
import org.qbrp.client.core.mc.skin.SkinTextureManager
import org.qbrp.client.render.screen.context.Draw2dContext
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.EngineModule
import org.qbrp.main.engine.players.model.PlayerProfile

@Autoload(env = EnvType.CLIENT)
class PlayerHeadsModule: EngineModule("player-heads"), PlayerHeadsRenderer {
    private val skinTextureManager: SkinTextureManager by inject()
    override fun getKoinModule(): Module = onlyApi<PlayerHeadsRenderer>(this)

    override fun renderHeadTexture(ctx: Draw2dContext, player: PlayerProfile, x1: Int, y1: Int, scale: Float) {
        val texture = skinTextureManager.getSkinId(player.minecraftPlayerId)
        renderClippedTexture(ctx, texture, x1, y1, scale)
    }

    private fun renderClippedTexture(ctx: Draw2dContext, texture: Identifier, x1: Int, y1: Int, scale: Float) {
        RenderSystem.setShaderTexture(0, texture)
        ctx.matrices.push()
        ctx.matrices.translate(x1.toFloat(), y1.toFloat(), 0f)
        ctx.matrices.scale(scale, scale, 1f)
        ctx.drawTexture(
            texture,
            0f, 0f,
            8f, 8f,
            8f, 8f,
            64f, 64f
        )
        ctx.matrices.pop()
    }
}