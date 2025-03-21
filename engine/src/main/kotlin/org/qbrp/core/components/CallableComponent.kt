package org.qbrp.core.components

interface CallableComponent {
    fun call(context: CallContext): String
}