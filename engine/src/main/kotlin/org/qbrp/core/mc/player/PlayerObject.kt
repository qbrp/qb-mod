package org.qbrp.core.mc.player

import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import org.qbrp.core.game.lifecycle.Lifecycle
import org.qbrp.core.game.model.State
import org.qbrp.core.game.model.tick.Tick
import org.qbrp.core.mc.McObject
import org.qbrp.core.mc.player.model.AccountDatabaseService
import org.qbrp.core.mc.player.registration.PlayerRegistrationCallback
import org.qbrp.engine.players.characters.Character
import org.qbrp.engine.players.characters.appearance.Appearance
import org.qbrp.engine.players.nicknames.NicknamesModule
import org.qbrp.system.utils.format.Format.asMiniMessage

class PlayerObject(
    val entity: ServerPlayerEntity,
    var account: Account,
    override var state: State = State()
): McObject(entity.name.string, PlayerManager.lifecycleManager as Lifecycle<McObject>) {

    val database = Database(PlayerManager.accountDatabase)
    val handler = PlayerHandler(this)
    var speed: Int? = null

    override val pos: Vec3d
        get() = entity.pos
    val playerName: String
        get() = entity.name.string

    override fun getTooltip(): String {
        return "$displayName &d (&r ${state.getComponentOrThrow<Appearance>().tooltip} &d)"
    }

    override fun tick() {
        state.tickables.forEach { tickable ->
            @Suppress("UNCHECKED_CAST")
            (tickable as Tick<World>).tick(entity.world)
        }
    }

    fun setSpeed(speed: Int) {
        this.speed = speed
    }

    fun resetSpeed() {
        this.speed = null
    }

    val displayName: String
        get() = state.getComponent<NicknamesModule.NicknameManager>()?.getDisplayName()
            ?: entity.name.string

    val displayNameText: Text
        get() = displayName.asMiniMessage()

    fun executeCommand(command: String) {
        entity.server.commandManager.executeWithPrefix(entity.commandSource, command)
    }

    fun getLookingAt() = PlayerManager.getPlayerLookingAt(this.entity)

    inner class Database(val service: AccountDatabaseService) {

        fun updateAccount(account: Account) {
            try {
                this@PlayerObject.account = account.apply {
                    updateRegisteredNicknames(this@PlayerObject.entity.name.string)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        suspend fun upsertAccount() {
            updateAccount(service.get(this@PlayerObject.account.uuid)!!)
        }
    }
}
