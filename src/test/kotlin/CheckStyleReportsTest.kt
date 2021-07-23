import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import systems.danger.kotlin.sdk.DangerContext
import kotlin.test.fail


internal class CheckStyleReportsTest {
    private val mockContext = mockk<DangerContext>(relaxed = true)

    @BeforeEach
    fun setup() {
        CheckStyleReports.context = mockContext
    }

    @Test
    fun `when file doesn't exists throws error`() {
        try {
            CheckStyleReports.checkstyle(reportsPath = "a.xml")
            fail("Should fail as file doesn't exists")
        } catch (throwable: Throwable) {
        }
    }


    @Test
    fun `when multiple files doesn't match anything then throws`() {
        try {
            CheckStyleReports.checkstyle(reportsPath = "**/*/a.xml")
            fail("Should fail as no file exists")
        } catch (throwable: Throwable) {
            print(throwable)
        }
    }

    @Test
    fun `when multiple files then parses every one`() {
        CheckStyleReports.checkstyle(reportsPath = "src/test/resources/multiple-files/**/*/checkstyle.xml")

        verify {
            allWarns.forEach {
                mockContext.warn(it["message"] as String, it["file"] as String, it["line"] as Int)
            }
        }
        confirmVerified(mockContext)
    }

    private val allWarns = arrayOf(
        mapOf(
            "message" to "Missing newline after \":\"",
            "line" to 27,
            "file" to "/path/to/ExampleClass.kt"
        ),
        mapOf(
            "message" to "Missing newline after \":\"",
            "line" to 36,
            "file" to "/path/to/another/AnotherExampleClass.kt"
        ),
        mapOf(
            "message" to "Missing newline after \":\"",
            "line" to 27,
            "file" to "/path/to/another/AnotherExampleClass.kt"
        ),
        mapOf(
            "message" to "Missing newline after \":\"",
            "line" to 27,
            "file" to "/path/second/to/ExampleClass.kt"
        ),
        mapOf(
            "message" to "Missing newline after \":\"",
            "line" to 27,
            "file" to "/path/second/to/another/AnotherExampleClass.kt"
        ),
        mapOf(
            "message" to "Missing newline after \":\"",
            "line" to 36,
            "file" to "/path/second/to/another/AnotherExampleClass.kt"
        ),
    )
}