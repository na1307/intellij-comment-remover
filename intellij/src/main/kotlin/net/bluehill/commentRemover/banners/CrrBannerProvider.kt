package net.bluehill.commentRemover.banners

import com.intellij.ide.plugins.PluginEnabler
import com.intellij.ide.plugins.PluginManager
import com.intellij.ide.plugins.PluginManagerMain
import com.intellij.ide.plugins.PluginNode
import com.intellij.openapi.application.ModalityState
import com.intellij.openapi.editor.colors.ColorKey
import com.intellij.openapi.extensions.PluginId
import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.EditorNotificationPanel
import com.intellij.ui.EditorNotificationProvider
import com.intellij.ui.JBColor
import net.bluehill.commentRemover.CrBundle
import java.util.function.Function
import javax.swing.JComponent

class CrrBannerProvider : EditorNotificationProvider {
    override fun collectNotificationData(project: Project, vf: VirtualFile): Function<in FileEditor, out JComponent?> =
        Function { fileEditor -> getBanner(fileEditor) }

    private fun getBanner(fileEditor: FileEditor): EditorNotificationPanel? {
        val riderPluginId = PluginId.getId("net.bluehill.commentRemover.csvbcpp")

        if (PluginManager.getInstance().findEnabledPlugin(riderPluginId) != null) {
            return null
        }

        val panel = EditorNotificationPanel(
            fileEditor,
            JBColor.blue,
            ColorKey.find("blue"),
            EditorNotificationPanel.Status.Info
        )

        panel.text = CrBundle.message("riderPluginRequired")
        
        panel.createActionLabel(CrBundle.message("installRiderPlugin")) {
            PluginManagerMain.downloadPlugins(
                listOf(PluginNode(riderPluginId)),
                emptyList(),
                true,
                null,
                PluginEnabler.getInstance(),
                ModalityState.nonModal(),
                null
            )
        }

        panel.setCloseAction { panel.isVisible = false }

        return panel
    }
}
