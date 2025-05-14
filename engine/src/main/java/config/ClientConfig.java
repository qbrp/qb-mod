package config;

import eu.midnightdust.lib.config.MidnightConfig;

public class ClientConfig extends MidnightConfig {
    @Entry(category = "account", name = "Код")
    public static String accountCode = "NONE";

    @Entry(category = "chat", name = "Вместимость чата")
    public static int chatSize = 1000;

    @Comment(category = "chat", centered = true) public static Comment optimization;

    @Entry(category = "chat", name = "Частота обновления сообщений")
    public static int chatTickRate = 5;

    @Entry(category = "chat", name = "Фильтрация обработанных сообщений")
    public static boolean filterHandledMessages = true;

    @Entry(category = "chat", name = "Обновление сообщений при получении")
    public static boolean handleMessagesOnReceive = true;

}
