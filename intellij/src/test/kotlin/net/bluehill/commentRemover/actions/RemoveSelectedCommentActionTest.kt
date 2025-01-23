package net.bluehill.commentRemover.actions

import com.intellij.testFramework.fixtures.BasePlatformTestCase

class RemoveSelectedCommentActionTest : BasePlatformTestCase() {
    fun testRemoveSelectedCommentWithoutSelection() {
        myFixture.configureByText(
            "test.kt", """
            // This is a comment
            fun main() {
                // This is a comment
                println("Hello, world!")
                // This is a comment
            }
        """.trimIndent()
        )

        myFixture.testAction(RemoveSelectedCommentAction())

        myFixture.checkResult(
            """
            // This is a comment
            fun main() {
                // This is a comment
                println("Hello, world!")
                // This is a comment
            }
        """.trimIndent()
        )
    }

    fun testRemoveSelectedCommentWithSelection() {
        myFixture.configureByText(
            "test.kt", """
            // This is a comment
            fun main() {
                // This is a comment
                println("Hello, world!")
                // This is a comment
            }
        """.trimIndent()
        )

        myFixture.editor.selectionModel.setSelection(0, 20)

        myFixture.testAction(RemoveSelectedCommentAction())

        myFixture.checkResult(
            """
            fun main() {
                // This is a comment
                println("Hello, world!")
                // This is a comment
            }
        """.trimIndent()
        )
    }
}
