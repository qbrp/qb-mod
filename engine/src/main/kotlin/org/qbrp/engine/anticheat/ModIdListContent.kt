package org.qbrp.engine.anticheat

import org.qbrp.system.networking.messages.types.ListContent

class ModIdListContent() : ListContent<String>(
    writer = { buf, str -> buf.writeString(str) },
    reader = { buf      -> buf.readString(32767) }
) {
}