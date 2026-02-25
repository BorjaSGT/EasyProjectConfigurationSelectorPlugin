package com.bsg.easyconfig.actions

import com.bsg.easyconfig.services.EnvironmentConfigurationService
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.actionSystem.ex.ComboBoxAction
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager
import javax.swing.JComponent

class EnvironmentComboBoxAction : ComboBoxAction(), DumbAware {

    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT

    override fun createPopupActionGroup(button: JComponent, context: DataContext): DefaultActionGroup {
        val group = DefaultActionGroup()
        val project = context.getData(CommonDataKeys.PROJECT) ?: getCurrentProject() ?: return group

        val service = EnvironmentConfigurationService.getInstance(project)
        val environments = service.getEnvironments()

        if (environments.isEmpty()) {
            group.add(object : AnAction("(No environments)") {
                override fun actionPerformed(e: AnActionEvent) {}
                override fun update(e: AnActionEvent) {
                    e.presentation.isEnabled = false
                }
            })
        } else {
            environments.forEach { env ->
                group.add(SelectEnvironmentAction(env.name))
            }
        }

        return group
    }

    override fun update(e: AnActionEvent) {
        val project = e.project ?: getCurrentProject()
        if (project == null) {
            e.presentation.isEnabledAndVisible = false
            return
        }

        e.presentation.isEnabledAndVisible = true
        val service = EnvironmentConfigurationService.getInstance(project)
        val selectedEnv = service.getSelectedEnvironment()
        e.presentation.text = if (selectedEnv.isNotEmpty()) selectedEnv else "No Environment"
    }

    private fun getCurrentProject(): Project? {
        return ProjectManager.getInstance().openProjects.firstOrNull()
    }

    private class SelectEnvironmentAction(private val envName: String) : AnAction(envName), DumbAware {

        override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT

        override fun actionPerformed(e: AnActionEvent) {
            val project = e.project ?: return
            val service = EnvironmentConfigurationService.getInstance(project)
            service.applyEnvironment(envName)
        }

        override fun update(e: AnActionEvent) {
            val project = e.project ?: return
            val service = EnvironmentConfigurationService.getInstance(project)
            val isSelected = service.getSelectedEnvironment() == envName
            e.presentation.icon = if (isSelected) com.intellij.icons.AllIcons.Actions.Checked else null
        }
    }
}
