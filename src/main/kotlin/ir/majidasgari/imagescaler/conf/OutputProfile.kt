package ir.majidasgari.imagescaler.conf

data class OutputProfile(
        var width: Int,
        var height: Int,
        var path: String,
        var input: String? = AUTO_SELECT,
        var function: String? = FUNCTION_SCALE_AND_CROP,
        var quality: Double? = null,
) {
    companion object {
        const val AUTO_SELECT = "auto-select"
        const val FUNCTION_SCALE_AND_CROP = "scaleAndCrop"
    }
}