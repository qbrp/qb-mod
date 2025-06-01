import PermissionsUtil.hasPermission
import kotlinx.coroutines.*
import net.minecraft.util.ActionResult
import org.qbrp.main.engine.chat.ChatAddon
import org.qbrp.main.engine.chat.core.events.MessageReceivedEvent
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.LoadPriority
import java.util.concurrent.ConcurrentHashMap

@Autoload(LoadPriority.ADDON)
class Limitation: ChatAddon("limitation") {
    private val limitationMap = ConcurrentHashMap<String, Int>()
    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    override fun onLoad() {
        MessageReceivedEvent.EVENT.register { message ->
            val player = message.getAuthorEntity() ?: return@register ActionResult.PASS
            if (player.hasPermission("chat.limit.ignore") == true) {
                val playerName = message.getAuthorEntity()?.name?.string ?: return@register ActionResult.PASS
                val limit = message.getTags().getComponentData<Double>("limit") ?: return@register ActionResult.PASS
                val limitTime =
                    message.getTags().getComponentData<Double>("limitTime") ?: return@register ActionResult.PASS

                limitationMap[playerName] = limitationMap.getOrDefault(playerName, 0) + 1

                if (limitationMap[playerName]!! >= limit) {
                    message.sendException("Вы превысили лимит в ${limit.toInt()} сообщений. Пожалуйста, подождите ${limitTime.toInt()} минут.")
                    scheduleLimitReset(playerName, limitTime.toInt())
                    return@register ActionResult.FAIL
                }
            }
            ActionResult.PASS
        }
    }

    private fun scheduleLimitReset(playerName: String, limitTime: Int) {
        coroutineScope.launch {
            delay(limitTime * 60 * 1000L) // Перевод минут в миллисекунды
            limitationMap.remove(playerName)
        }
    }
}
