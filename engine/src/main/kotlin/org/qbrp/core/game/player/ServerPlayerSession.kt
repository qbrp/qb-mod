package org.qbrp.core.game.player

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonSetter
import com.fasterxml.jackson.annotation.Nulls
import com.mongodb.client.model.Filters
import net.minecraft.entity.attribute.EntityAttributeModifier
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import org.qbrp.core.game.player.ServerPlayerSession.Account
import org.qbrp.core.game.player.registration.LoginResult
import org.qbrp.system.database.DatabaseService
import org.qbrp.system.networking.messages.Messages
import org.qbrp.system.networking.messaging.NetworkManager
import org.qbrp.system.utils.format.Format.asMiniMessage
import java.util.UUID

data class ServerPlayerSession(
    val entity: ServerPlayerEntity,
    var speed: Int? = 1
) {
    lateinit var account: Account
    val database = Database()
    val handler = PlayerHandler(this)

    fun onDisconnect() {
        saveAccountIfExists()
    }

    fun onConnect() {
        sendRegistrationMessage()
    }

    private fun saveAccountIfExists() {
        if (::account.isInitialized) { database.save() }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    class Account(@JsonSetter(nulls = Nulls.AS_EMPTY) val minecraftNicknames: MutableList<String>,
                  var displayName: String = minecraftNicknames.first(),
                  val uuid: UUID = UUID.randomUUID(),
    ) {

        fun updateDisplayName(newName: String) { displayName = newName }

        fun updateRegisteredNicknames(name: String) {
            if (minecraftNicknames.contains(name)) { minecraftNicknames.add(name) }
        }

        companion object {
            fun new(player: ServerPlayerSession) = Account(mutableListOf(player.entity.name.string))
        }
    }

    inner class Database(val service: DatabaseService = PlayerManager.databaseService) {

        fun save() {
            service.upsertObject<Account>(
                "data",
                mapOf("uuid" to account.uuid.toString()),
                account)
        }

        fun get(uuid: UUID): Account? {
            val matches = service.fetchAll("data", mapOf("uuid" to uuid.toString()), Account::class.java)
            return matches.firstOrNull() as Account?
        }

        fun isNicknameAlreadyRegistered(nickname: String): Boolean {
            val matches = service.fetchAll(
                document = "data",
                query = emptyMap(),  // Можно оставить пустым, так как фильтр будет через customFilters
                clazz = Account::class.java,
                customFilters = listOf(Filters.eq("nicknames", nickname)) // nicknames — это массив в документе
            )
            return matches.isNotEmpty()
        }

        fun login(uuid: UUID, nickname: String): LoginResult {
            get(uuid)?.let {
                if (!isNicknameAlreadyRegistered(nickname)) {
                    account = it
                    account.updateRegisteredNicknames(nickname)
                    return LoginResult.SUCCESS
                } else {
                    return LoginResult.ALREADY_LOGGED_IN
                }
            }
            return LoginResult.NOT_FOUND
        }

        fun register() {
            account = Account.new(this@ServerPlayerSession)
            save()
        }
    }

    private fun sendRegistrationMessage() = NetworkManager.sendSignal(entity, Messages.REGISTRATION_REQUEST)

    fun setSpeed(speed: Int) {
        this.speed = speed
    }

    fun resetSpeed() { this.speed = null }

    fun getDisplayName(): String = if (!::account.isInitialized) entity.name.string else account.displayName

    fun getDisplayNameText(): Text = if (!::account.isInitialized) entity.name else account.displayName.asMiniMessage()
}
