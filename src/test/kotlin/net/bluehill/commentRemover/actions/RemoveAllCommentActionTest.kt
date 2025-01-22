package net.bluehill.commentRemover.actions

import com.intellij.testFramework.fixtures.BasePlatformTestCase

class RemoveAllCommentActionTest : BasePlatformTestCase() {
    fun testRemoveAllComment() {
        myFixture.configureByText(
            "test.kt", """
            // comment
            fun main() {
                // comment
                println("Hello, world!")
                // comment
            }
        """.trimIndent()
        )

        myFixture.testAction(RemoveAllCommentAction())

        myFixture.checkResult(
            """
            fun main() {
                println("Hello, world!")
            }
        """.trimIndent()
        )
    }
}
