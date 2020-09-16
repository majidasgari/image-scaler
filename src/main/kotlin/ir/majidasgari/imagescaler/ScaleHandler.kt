package ir.majidasgari.imagescaler

import ir.majidasgari.imagescaler.conf.Configuration
import ir.majidasgari.imagescaler.conf.OutputProfile
import ir.majidasgari.imagescaler.conf.ScaleEngine
import ir.majidasgari.imagescaler.utils.*
import net.coobird.thumbnailator.Thumbnails
import net.coobird.thumbnailator.geometry.Positions
import org.imgscalr.Scalr
import java.nio.file.Files
import java.nio.file.Path
import javax.imageio.ImageIO
import kotlin.math.abs
import kotlin.system.exitProcess

fun main() {
    //TODO: You ca remove following line. It copy the default config file in the working directory
    ResourceFileCopier.copy("config.json")
    val config = TextFileReader.readJSON("config.json", Configuration::class.java)!!
    val rootPath = config.rootFolder.toPath()

    data class InputFile(val path: Path, val dimension: ImageDimension)

    val inputFiles = mutableMapOf<String, InputFile>()
    for (filename in config.inputs) {
        val path = filename.toPath(rootPath)
        try {
            ResourceFileCopier.copy(filename, path)
        } catch (th: Throwable) {
            println("error in reading input file ${path.toAbsolutePath()}")
            th.printStackTrace()
            exitProcess(1)
        }
        inputFiles[filename] = InputFile(path, path.getImageDimension())
        println("reading input file ${inputFiles[filename]}")
    }

    for (it in config.outputs) {
        val aspectRatioDst = it.width.toDouble() / it.height
        println("convert output ${it.input} for ${it.width}x${it.height}.")
        val inputImage = if (it.input == null || it.input == OutputProfile.AUTO_SELECT) {
            val bestFiles = arrayOf<InputFile?>(null, null) // best portrait, best landscape
            inputFiles.forEach { key, value ->
                val i = if (value.dimension.portrait) 0 else 1
                if (bestFiles[i] == null ||
                        abs(bestFiles[i]!!.dimension.aspectRatio - aspectRatioDst) <
                        abs(value.dimension.aspectRatio - aspectRatioDst))
                    bestFiles[i] = value
            }
            val portrait = it.width < it.height
            if (portrait && bestFiles[0] != null) bestFiles[0]
            else if (!portrait && bestFiles[1] != null) bestFiles[1]
            else if (bestFiles[0] != null) bestFiles[0]
            else bestFiles[1]
        } else inputFiles[it.input]
        print("source image is ${inputImage?.path}")
        println(inputImage!!)
        val outputPath = it.path.toPath(rootPath)
        Files.createDirectories(outputPath.parent)
        println("write output $outputPath for ${it.width}x${it.height}.")

        if (config.engine == ScaleEngine.thumbnailator) {
            val builder = Thumbnails.of(inputImage.path.toFile())
            builder.crop(Positions.CENTER).size(it.width, it.height)
            if (it.quality != null) builder.outputQuality(it.quality!!)
            builder.toFile(outputPath.toFile())
        } else {
            if (aspectRatioDst > inputImage.dimension.aspectRatio) {
                val nsH = inputImage.dimension.height * it.width / inputImage.dimension.width
                ImageIO.write(Scalr.crop(
                        Scalr.resize(ImageIO.read(inputImage.path.toFile()), Scalr.Mode.AUTOMATIC, it.width, nsH),
                        0, abs(nsH - it.height) / 2, it.width, it.height),
                        outputPath.fileName.toString().substringAfterLast('.'), outputPath.toFile())
            } else {
                val nsW = inputImage.dimension.width * it.height / inputImage.dimension.height
                ImageIO.write(Scalr.crop(
                        Scalr.resize(ImageIO.read(inputImage.path.toFile()), Scalr.Mode.AUTOMATIC, nsW, it.height),
                        abs(nsW - it.width) / 2, 0, it.width, it.height),
                        outputPath.fileName.toString().substringAfterLast('.'), outputPath.toFile())
            }
        }
    }
}