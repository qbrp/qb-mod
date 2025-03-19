package org.qbrp.engine.time
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.qbrp.core.resources.data.config.ServerConfigData
import org.qbrp.engine.Engine
import org.qbrp.engine.chat.addons.BroadcasterAPI

class TimeNotifications(val config: ServerConfigData.Time): KoinComponent {
    val broadcaster = Engine.getAPI<BroadcasterAPI>()

    fun broadcastTimeDo(time: Int, name: String) {
//        broadcaster.broadcast( Text.empty()
//            .append(Text.literal("* ").setStyle(Style.EMPTY.withBold(true).withColor(Formatting.LIGHT_PURPLE)))
//            .append(Text.literal("Мир ").setStyle(Style.EMPTY.withColor(0xC0A1FF)))
//            .append(Text.literal("( ").setStyle(Style.EMPTY.withColor(Formatting.LIGHT_PURPLE)))
//            .append(Text.literal(Time.minutesToTime(Time.roundMinutesToNearestHalfHour(time))).setStyle(Style.EMPTY.withBold(true).withColor(0xC6A6FF)))
//            .append(Text.literal(" ${name}.").setStyle(Style.EMPTY.withColor(0xC0A1FF)))
//            .append(Text.literal(" )").setStyle(Style.EMPTY.withColor(Formatting.LIGHT_PURPLE)))
//        )
        TODO()
    }
}