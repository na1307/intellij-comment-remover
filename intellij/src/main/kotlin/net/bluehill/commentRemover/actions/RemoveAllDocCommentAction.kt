package net.bluehill.commentRemover.actions

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiDocCommentBase

class RemoveAllDocCommentAction : RemoveAllCommentAction() {
    override fun removeComment(proj: Project, comments: Iterable<PsiComment>) =
        super.removeComment(proj, comments.filterIsInstance<PsiDocCommentBase>())
}
