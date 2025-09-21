package net.bluehill.commentRemover.actions

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.psi.PsiComment
import com.intellij.psi.util.PsiTreeUtil
import net.bluehill.commentRemover.CrBundle

abstract class RemoveCommentAction : AnAction() {
    final override fun update(event: AnActionEvent) {
        event.presentation.isEnabledAndVisible = event.project !== null
    }

    final override fun actionPerformed(event: AnActionEvent) {
        val proj = event.project

        if (proj == null) {
            Messages.showErrorDialog(CrBundle.message("projectIsNull"), "Error")
            return
        }

        val pf = event.getData(CommonDataKeys.PSI_FILE)

        if (pf == null) {
            Messages.showErrorDialog(proj, CrBundle.message("psiFileIsNull"), "Error")
            return
        }

        val comments = PsiTreeUtil.collectElementsOfType(pf, PsiComment::class.java)

        if (comments.isEmpty()) {
            // Comments not found
            return
        }

        removeComment(proj, comments)
    }

    abstract fun removeComment(proj: Project, comments: Iterable<PsiComment>)

    final override fun getActionUpdateThread() = ActionUpdateThread.EDT
}
