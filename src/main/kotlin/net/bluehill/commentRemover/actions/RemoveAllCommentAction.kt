package net.bluehill.commentRemover.actions

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.ui.Messages
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil

class RemoveAllCommentAction : AnAction() {
    override fun update(event: AnActionEvent) {
        event.presentation.isEnabledAndVisible = event.project !== null
    }

    override fun actionPerformed(event: AnActionEvent) {
        val proj = event.project

        if (proj == null) {
            Messages.showErrorDialog("Project is null.", "Error")
            return
        }

        val pf = event.getData(CommonDataKeys.PSI_FILE)

        if (pf == null) {
            Messages.showErrorDialog(proj, "PSI_FILE is null.", "Error")
            return
        }

        val comments = PsiTreeUtil.collectElementsOfType(pf, PsiComment::class.java)

        if (comments.isEmpty()) {
            // Comments not found
            return
        }

        val editor = FileEditorManager.getInstance(proj).selectedTextEditor

        if (editor == null) {
            Messages.showErrorDialog(proj, "Editor is null.", "Error")
            return
        }

        val selection = findSelection(editor)

        WriteCommandAction.runWriteCommandAction(proj) {
            if (selection == null) {
                // No selection: Removes all comments from the file.
                comments.forEach { it.delete() }
            } else {
                // Removes comments from selected text.
                comments.filter { it.isInSelectedLocation(selection) }.forEach { it.delete() }
            }
        }
    }

    override fun getActionUpdateThread() = ActionUpdateThread.EDT

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
