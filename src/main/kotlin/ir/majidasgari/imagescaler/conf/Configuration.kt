package ir.majidasgari.imagescaler.conf

data class Configuration (
        val rootFolder: String,
        val engine: ScaleEngine,
        val inputs: MutableList<String> = mutableListOf(),
        val outputs: MutableList<OutputProfile> = mutableListOf(),
)