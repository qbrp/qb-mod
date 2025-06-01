package org.qbrp.main.core.utils.networking.messages

object Messages {
    const val START_TYPING = "start_typing"
    const val END_TYPING = "end_typing"

    const val SEND_MESSAGE = "send_message"
    const val HANDLE_VERSION = "handle_version"

    const val SERVER_INFORMATION = "server_information"

    const val AUTH = "registration_response"

    const val INVOKE_COMMAND = "invoke_command"

    const val MOD_IDS = ""

    fun invokeCommand(name: String): String = "invoke_command_$name"
    fun moduleUpdate(name: String): String = "module_update_$name"
    fun moduleClientUpdate(name: String): String = "module_update_${name}-client"
}