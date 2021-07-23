package ext

import java.nio.file.Path
import kotlin.io.path.pathString

/**
 * @return String representation of path with a trailing Slash (use with caution)
 */
fun Path.withTrailingSlashIfNeeded(): String {
    val path = pathString
    if (path.endsWith("/")) {
        return path
    }
    return "$path/"
}