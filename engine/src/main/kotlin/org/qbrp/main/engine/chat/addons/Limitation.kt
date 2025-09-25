import kotlinx.coroutines.*
import net.minecraft.util.ActionResult
import org.qbrp.main.core.mc.player.permission.Permission
import org.qbrp.main.core.mc.player.hasPermission
import org.qbrp.main.engine.chat.ChatAddon
import org.qbrp.main.engine.chat.core.events.MessageReceivedEvent
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.Priorities
import org.qbrp.main.engine.players.model.util.PlayerId
import java.util.concurrent.ConcurrentHashMap

@Autoload(Priorities.ADDON)
class Limitation: ChatAddon("limitation") {
    private val limitationMap = ConcurrentHashMap<PlayerId, Int>()
    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    override fun onLoad() {
        MessageReceivedEvent.EVENT.register { message ->
            val author = message.author ?: return@register ActionResult.PASS
            if (author.hasPermission(Permission("chat.limit.ignore"))) {
                val playerId = author.playerId
                val limit = message.getTags().getComponentData<Double>("limit") ?: return@register ActionResult.PASS
                val limitTime =
                    message.getTags().getComponentData<Double>("limitTime") ?: return@register ActionResult.PASS

                limitationMap[playerId] = limitationMap.getOrDefault(playerId, 0) + 1

                if (limitationMap[playerId]!! >= limit) {
                    message.sendException("Вы превысили лимит в ${limit.toInt()} сообщений. Пожалуйста, подождите ${limitTime.toInt()} минут.")
                    scheduleLimitReset(playerId, limitTime.toInt())
                    return@register ActionResult.FAIL
                }
            }
            ActionResult.PASS
        }
    }

    private fun scheduleLimitReset(playerId: PlayerId, limitTime: Int) {
        coroutineScope.launch {
            delay(limitTime * 60 * 1000L) // Перевод минут в миллисекунды
            limitationMap.remove(playerId)
        }
    }
}
