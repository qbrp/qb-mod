package org.qbrp.core.game.player

import com.fasterxml.jackson.annotation.JsonIgnore
import com.mongodb.client.model.Filters
import com.mongodb.client.model.UpdateOptions
import com.mongodb.client.model.Updates
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.io.UnsafeIoApi
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import org.qbrp.core.game.player.interaction.InteractionManager
import org.qbrp.core.game.player.registration.LoginResult
import org.qbrp.core.game.player.registration.PlayerRegistrationCallback
import org.qbrp.system.database.DatabaseService
import org.qbrp.system.networking.messages.Messages
import org.qbrp.system.networking.messaging.NetworkManager
import org.qbrp.system.utils.format.Format.asMiniMessage
import java.util.UUID

data class ServerPlayerSession(
    val entity: ServerPlayerEntity,
    var speed: Int? = 1
) {
    var account: Account? = null
    val database = Database()
    val handler = PlayerHandler(this)
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    val interactionManager = InteractionManager(this)

    fun onDisconnect() {
        scope.launch {
            if (isAuthorized()) database.saveGameState()
        }
    }

    fun onConnect() {
        sendRegistrationMessage()
    }

    fun isAuthorized() = account != null

    private fun sendRegistrationMessage() =
        NetworkManager.sendSignal(entity, Messages.REGISTRATION_REQUEST)

    fun setSpeed(speed: Int) {
        this.speed = speed
    }

    fun resetSpeed() {
        this.speed = null
    }

    var customDisplayName: String? = null

    @get:JsonIgnore
    val displayName: String
        get() = customDisplayName
            ?: account?.appliedCharacter?.formattedName
            ?: account?.minecraftNicknames?.last()
            ?: entity.name!!.string
            ?: "Unknown"

    @get:JsonIgnore
    val displayNameText: Text
        get() = displayName.asMiniMessage()

    fun updateCustomName(newName: String) { customDisplayName = newName }

    fun resetCustomName() { customDisplayName = null }

    fun executeCommand(command: String) {
        entity.server.commandManager.executeWithPrefix(entity.commandSource, command)
    }

    fun getLookingAt() = PlayerManager.getPlayerLookingAt(this.entity)

    inner class Database(val service: DatabaseService = PlayerManager.databaseService) {

        @UnsafeIoApi
        suspend fun save() = withContext(Dispatchers.IO) {
            service.upsertObject<Account>(
                "data",
                mapOf("uuid" to account!!.uuid.toString()),
                account!!
            )
        }

        suspend fun saveGameState() = withContext(Dispatchers.IO) {
            val accountUuid = account!!.uuid.toString()
            val characterName = account!!.appliedCharacterName
            val filter = Filters.eq("uuid", accountUuid)
            val update = Updates.combine(
                Updates.set("appliedCharacterName", characterName),
                Updates.set("minecraftNicknames", account!!.minecraftNicknames),
                Updates.set("characters.$[elem].appliedLook", account!!.appliedCharacter!!.appearance.look),
                Updates.set("social", account!!.social)
            )
            val options = UpdateOptions().arrayFilters(listOf(Filters.eq("elem.name", characterName)))
            service.db!!.getCollection("data").updateOne(filter, update, options)
        }

        suspend fun get(uuid: UUID): Account? = withContext(Dispatchers.IO) {
            val matches = service.fetchAll("data", mapOf("uuid" to uuid.toString()), Account::class.java)
            matches.firstOrNull() as Account?
        }

        suspend fun isNicknameAlreadyRegistered(nickname: String): Boolean =
            withContext(Dispatchers.IO) {
                val matches = service.fetchAll(
                    document = "data",
                    query = emptyMap(),
                    clazz = Account::class.java,
                    customFilters = listOf(Filters.eq("nicknames", nickname))
                )
                matches.isNotEmpty()
            }

        suspend fun login(uuid: UUID, nickname: String): LoginResult = withContext(Dispatchers.IO) {
            get(uuid)?.let {
                if (!isNicknameAlreadyRegistered(nickname)) {
                    authorize(it)
                    LoginResult.SUCCESS
                } else {
                    LoginResult.ALREADY_LINKED
                }
            } ?: LoginResult.NOT_FOUND
        }

        fun updateAccount(account: Account) {
            try {
                this@ServerPlayerSession.account = account.apply {
                    updateRegisteredNicknames(this@ServerPlayerSession.entity.name.string)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        suspend fun upsertAccount() {
            updateAccount(get(account!!.uuid)!!)
        }

        private suspend fun authorize(account: Account) = withContext(Dispatchers.IO) {
            updateAccount(account)
            PlayerRegistrationCallback.EVENT.invoker().onRegister(this@ServerPlayerSession, PlayerManager)
        }
    }
}
