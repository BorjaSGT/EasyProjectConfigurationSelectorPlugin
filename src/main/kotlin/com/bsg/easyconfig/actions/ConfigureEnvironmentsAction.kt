package com.bsg.easyconfig.actions

import com.bsg.easyconfig.ui.EnvironmentConfigDialog
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent

class ConfigureEnvironmentsAction : AnAction() {
    
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        EnvironmentConfigDialog(project).show()
    }
}
