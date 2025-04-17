package org.qbrp.system.networking.messages

object Messages {
    const val GET_CHUNK_VISUAL = "get_chunk_visual"
    const val LOAD_CHUNK_VISUAL = "load_chunk_visual"
    const val UPDATE_VISUAL = "update_visual"

    const val START_TYPING = "start_typing"
    const val END_TYPING = "end_typing"

    const val SEND_MESSAGE = "send_message"
    const val HANDLE_VERSION = "handle_version"

    const val SERVER_INFORMATION = "server_information"

    const val REGISTRATION_REQUEST = "request_registration"
    const val REGISTRATION_RESPONSE = "registration_response"

    fun invokeCommand(name: String): String = "invoke_command_$name"
    fun moduleUpdate(name: String): String = "module_update_$name"
    fun moduleClientUpdate(name: String): String = "module_update_${name}-client"
}