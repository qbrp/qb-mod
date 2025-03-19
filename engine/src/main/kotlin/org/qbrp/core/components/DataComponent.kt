package org.qbrp.core.components

data class DataComponent(
    val type: Class<*>,
    val data: Any
) {
    inline fun <reified T> cast(): T {
        if (!type.isAssignableFrom(T::class.java)) {
            throw ClassCastException("Cannot cast ${data::class.java.simpleName} to ${T::class.java.simpleName}")
        }
        return data as T
    }

    companion object {
        fun getDeserializer() = DataComponentsDeserializer()
    }
}