package net.bluehill.commentRemover.actions

import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiComment

class RemoveAllCommentAction : RemoveCommentAction() {
    override fun removeComment(proj: Project, comments: Iterable<PsiComment>) {
        WriteCommandAction.runWriteCommandAction(proj) { comments.forEach { it.delete() } }
    }
}
