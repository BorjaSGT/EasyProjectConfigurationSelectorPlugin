package com.bsg.easyconfig.actions

import com.bsg.easyconfig.ui.EnvironmentConfigDialog
import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAware

class ConfigureEnvironmentsToolbarAction : AnAction(
    "Configure Environments",
    "Open environment configuration dialog",
    AllIcons.General.Settings
), DumbAware {
    
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project
        if (project != null) {
            EnvironmentConfigDialog(project).show()
        }
    }
    
    override fun update(e: AnActionEvent) {
        val project = e.project
        e.presentation.isEnabledAndVisible = project != null
    }
}
