package org.qbrp.client.render.text.entity

@JvmInline
value class TextRenderId(val value: String) {
    companion object { val DEFAULT = TextRenderId("default") }
}
