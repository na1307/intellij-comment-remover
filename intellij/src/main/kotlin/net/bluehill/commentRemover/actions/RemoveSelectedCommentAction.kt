package net.bluehill.commentRemover.actions

import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiElement
import net.bluehill.commentRemover.CrBundle

open class RemoveSelectedCommentAction : RemoveCommentAction() {
    override fun removeComment(proj: Project, comments: Iterable<PsiComment>) {
        val editor = FileEditorManager.getInstance(proj).selectedTextEditor

        if (editor == null) {
            Messages.showErrorDialog(proj, CrBundle.message("editorIsNull"), "Error")
            return
        }

        val selection = findSelection(editor)

        WriteCommandAction.runWriteCommandAction(proj) {
            if (selection != null) {
                // Removes comments from selected text.
                comments.filter { it.isInSelectedLocation(selection) }.forEach { it.delete() }
            } else {
                // No selection: Do nothing.
            }
        }
    }

    private fun findSelection(editor: Editor): Pair<Int, Int>? {
        val model = editor.selectionModel

        return if (model.hasSelection()) model.selectionStart to model.selectionEnd else null
    }

    private fun PsiElement.isInSelectedLocation(pair: Pair<Int, Int>): Boolean {
        // Get the range of the PsiElement
        val elementStart = textRange.startOffset
        val elementEnd = textRange.endOffset

        // Check if the element's range is within the selection range
        return elementStart >= pair.first && elementEnd <= pair.second
    }
}
