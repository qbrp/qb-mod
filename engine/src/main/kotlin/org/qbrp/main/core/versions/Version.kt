package org.qbrp.main.core.versions

data class Version(
    val releaseStage: String,
    val major: Int,
    val minor: Int,
    val patch: Int,
    val fix: Int,
) {
    fun getBuild(): Int {
        return (major * 1_000_000) + (minor * 10_000) + (patch * 100) + fix
    }

    override fun toString(): String {
        return "${releaseStage}-${major}.${minor}.${patch}${if (fix != 0) "-$fix" else ""}"
    }

    companion object {
        fun fromString(string: String): Version {
            val parts = string.split("-")
            val releaseStage = parts[0]
            val version = parts[1].split(".")
            val fix = parts.getOrNull(2)?.toIntOrNull() ?: 0
            return Version(releaseStage, version[0].toInt(), version[1].toInt(), version[2].toInt(), fix)
        }
    }
}