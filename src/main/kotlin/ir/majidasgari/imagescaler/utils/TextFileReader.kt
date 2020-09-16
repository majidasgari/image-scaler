package ir.majidasgari.imagescaler.utils


import com.google.gson.Gson
import java.io.BufferedReader
import java.io.FileReader
import java.io.InputStreamReader
import java.lang.reflect.Type
import java.nio.file.Files
import java.nio.file.Paths

/**
 * It is a handy tool for reading config files.It finds the file in working directory, and if it couldn't
 * find the file, read it from resources. Therefore you MUST have a file with the same name in your
 * resource folder.
 * @author Majid Asgari-Bidhendi (Dr.Coder)
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
object TextFileReader {

    private val gson = Gson()

    fun readLines(configFileName: String): List<String>? {
        try {
            val lines = mutableListOf<String>()
            if (Files.exists(Paths.get(configFileName))) {
                FileReader(configFileName).use { reader ->
                    BufferedReader(reader).use { bufferedReader ->
                        while (true) {
                            val line = bufferedReader.readLine() ?: break
                            lines.add(line)
                        }
                    }
                }
            } else {
                TextFileReader::class.java.classLoader.getResourceAsStream(configFileName).use { stream ->
                    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
                    InputStreamReader(stream).use { reader ->
                        BufferedReader(reader).use { bufferedReader ->
                            while (true) {
                                val line = bufferedReader.readLine() ?: break
                                lines.add(line)
                            }
                        }
                    }
                }
            }
            return lines
        } catch (th: Throwable) {
            println("error in file loading")
            th.printStackTrace()
            return null
        }
    }

    fun readString(configFileName: String): String? {
        try {
            val builder = StringBuilder()
            if (Files.exists(Paths.get(configFileName))) {
                FileReader(configFileName).use { reader ->
                    BufferedReader(reader).use { bufferedReader ->
                        while (true) {
                            val line = bufferedReader.readLine() ?: break
                            builder.append(line).append('\n')
                        }
                    }
                }
            } else {
                TextFileReader::class.java.classLoader.getResourceAsStream(configFileName).use { stream ->
                    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
                    InputStreamReader(stream).use { reader ->
                        BufferedReader(reader).use { bufferedReader ->
                            while (true) {
                                val line = bufferedReader.readLine() ?: break
                                builder.append(line).append('\n')
                            }
                        }
                    }
                }
            }
            return builder.toString()
        } catch (th: Throwable) {
            println("error in file loading")
            th.printStackTrace()
            return null
        }
    }

    fun <T> readJSON(configFileName: String, typeOfT: Type): T? {
        val content = readString(configFileName) ?: return null
        return gson.fromJson(content, typeOfT)
    }

    fun <T> readJSON(configFileName: String, classOfT: Class<T>): T? {
        val content = readString(configFileName) ?: return null
        return gson.fromJson(content, classOfT)
    }
}