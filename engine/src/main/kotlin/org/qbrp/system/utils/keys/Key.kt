package org.qbrp.system.utils.keys

open class Key(val name: String) {
    override fun toString(): String { return "UnitKey: $name" }

    override fun equals(other: Any?): Boolean {
        return other is Key && other.name == name
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }
}