package org.qbrp.main.engine.anticheat

import org.qbrp.main.core.utils.networking.messages.types.ListContent

class StringListContent() : ListContent<String>(
    writer = { buf, str -> buf.writeString(str) },
    reader = { buf      -> buf.readString(32767) }
) {
}