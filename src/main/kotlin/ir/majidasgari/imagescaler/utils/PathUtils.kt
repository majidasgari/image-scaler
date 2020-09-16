package ir.majidasgari.imagescaler.utils

import java.io.File
import java.nio.file.Path
import java.nio.file.Paths
import javax.imageio.ImageIO


data class ImageDimension(val width: Int, val height: Int, val aspectRatio: Double, val portrait: Boolean)

fun Path.getImageDimension() = ImageIO.read(toFile())!!.let {
    ImageDimension(it.width, it.height, it.width.toDouble() / it.height, it.width < it.height)
}

fun String.toPath(): Path =
        Paths.get(replace('/', File.separatorChar)).toAbsolutePath()

fun String.toPath(rootPath: Path): Path =
        rootPath.resolve(replace('/', File.separatorChar)).toAbsolutePath()