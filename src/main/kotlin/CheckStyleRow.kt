data class CheckStyleRow(
    val fileName: String,
    val line: Int,
    val column: Int?,
    val severity: String,
    val message: String,
    val source: String
)
