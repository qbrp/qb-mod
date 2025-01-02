package org.qbrp.system.utils.format

import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.nameWithoutExtension

fun getRelative(directoryName: String, path: Path): Path {
    val startIndex = path.iterator().asSequence().indexOfFirst { it.toString() == directoryName }
    return if (startIndex != -1) {
        path.subpath(startIndex + 1, path.nameCount)
    } else {
        throw IllegalArgumentException("Directory '$directoryName' not found in path: $path")
    }
}

fun String.getRelative(directoryName: String): String {
    val path = Paths.get(this)
    return getRelative(directoryName, path).toString()
}

fun String.removeExtensions(): String {
    val lastSlashIndex = lastIndexOf('/')
    val start = if (lastSlashIndex != -1) substring(0, lastSlashIndex + 1) else ""
    val fileName = substring(lastSlashIndex + 1)
    return start + fileName.substringBeforeLast('.', fileName)
}

fun Path.getExtension(): String {
    return this.toString().substringAfterLast('.', "")
}

fun String.getExtension(): String {
    return Paths.get(this).getExtension()
}

fun String.pathToJsonFormat(): String = this.replace("\\", "/")
fun Path.getModelType(): String = this.nameWithoutExtension.split('_').last()