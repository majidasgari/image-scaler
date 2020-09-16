package ir.majidasgari.imagescaler.utils

import java.io.*
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.jvm.Throws

object ResourceFileCopier {

    @Throws(Throwable::class)
    fun copy(resourceName: String, desiredPath: Path? = null) {
        val outputPath = desiredPath ?: Paths.get(resourceName)
        if(Files.exists(outputPath)) return
        ResourceFileCopier::class.java.classLoader.getResourceAsStream(resourceName).use { inputStream ->
            FileOutputStream(outputPath.toFile()).use { outputStream ->
                @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
                inputStream.copyTo(outputStream)
            }
        }
    }
}