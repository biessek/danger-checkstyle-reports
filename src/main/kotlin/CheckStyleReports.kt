import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import ext.withTrailingSlashIfNeeded
import systems.danger.kotlin.sdk.DangerPlugin
import java.io.FileInputStream
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Paths
import java.util.stream.Collectors
import kotlin.io.path.absolute
import kotlin.io.path.absolutePathString

object CheckStyleReports : DangerPlugin() {
    private val pwd by lazy {
        Paths.get("")
    }

    private val absoluteBasePath by lazy {
        pwd.absolute().withTrailingSlashIfNeeded()
    }

    override val id: String
        get() = this.javaClass.name

    fun checkstyle(reportsPath: String) {
        val matcher = FileSystems.getDefault().getPathMatcher("glob:$reportsPath")

        Files.walk(pwd)
            .filter { matcher.matches(it) }
            .collect(Collectors.toList())
            .forEach {
                val items = parse(it.absolutePathString())

                sendComments(items)
            }
    }

    private fun sendComments(items: List<CheckStyleRow>) {
        items.forEach {
            context.warn(
                message = it.message,
                file = it.fileName,
                line = it.line
            )
        }
    }

    private fun parse(absolutePath: String): List<CheckStyleRow> {
        val xmlMapper = XmlMapper()
        val root = xmlMapper.readTree(FileInputStream(absolutePath))

        val nodesList = root.get("file").filter { it.has("error") }.toList()
        return nodesList.flatMap { parentNode ->
            if (!parentNode.get("error").isArray) {
                return@flatMap listOf(parseNode(parentNode, parentNode.get("error")))
            } else {
                return@flatMap parentNode.get("error").map { child ->
                    parseNode(parentNode, child)
                }
            }
        }
    }

    private fun parseNode(
        parentNode: JsonNode,
        child: JsonNode
    ) = CheckStyleRow(
        fileName = parentNode.get("name").textValue().replace("^$absoluteBasePath".toRegex(), ""),
        line = child.get("line").textValue().toInt(),
        column = child.get("column").textValue()?.toInt(),
        severity = child.get("severity").textValue() ?: "",
        message = child.get("message").textValue() ?: "",
        source = child.get("source").textValue() ?: "",
    )

}