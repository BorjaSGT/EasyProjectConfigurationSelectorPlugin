package com.bsg.easyconfig.actions

import com.bsg.easyconfig.services.EnvironmentConfigurationService
import com.bsg.easyconfig.ui.EnvironmentConfigDialog
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.actionSystem.ex.ComboBoxAction
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.DumbAware
import javax.swing.JComponent

class EnvironmentSelectorAction : ComboBoxAction(), DumbAware {
    
    private val LOG = Logger.getInstance(EnvironmentSelectorAction::class.java)
    
    override fun update(e: AnActionEvent) {
        val project = e.project
        e.presentation.isEnabledAndVisible = project != null
        
        if (project != null) {
            val service = EnvironmentConfigurationService.getInstance(project)
            val selectedEnv = service.getSelectedEnvironment()
            val envCount = service.getEnvironments().size
            
            LOG.info("Update called: selectedEnv='$selectedEnv', envCount=$envCount")
            
            e.presentation.text = if (selectedEnv.isEmpty()) "Environment: None" else "Environment: $selectedEnv"
        } else {
            e.presentation.text = "Environment: None"
        }
    }
    
    override fun createPopupActionGroup(button: JComponent): DefaultActionGroup {
        val group = DefaultActionGroup()
        
        // Try to get project from multiple sources
        val project = com.intellij.openapi.project.ProjectManager.getInstance().openProjects.firstOrNull()
        
        if (project == null) {
            LOG.warn("No project found")
            group.add(ConfigureAction())
            return group
        }
        
        val service = EnvironmentConfigurationService.getInstance(project)
        val environments = service.getEnvironments()
        val currentEnv = service.getSelectedEnvironment()
        
        LOG.info("Creating popup: ${environments.size} environments found")
        environments.forEach { env ->
            LOG.info("  - ${env.name}")
        }
        
        if (environments.isEmpty()) {
            LOG.warn("No environments found, showing configure only")
            group.add(ConfigureAction())
        } else {
            environments.forEach { env ->
                group.add(SelectEnvironmentAction(env.name, env.name == currentEnv))
            }
            
            group.addSeparator()
            group.add(ConfigureAction())
        }
        
        return group
    }
    
    private class SelectEnvironmentAction(
        private val environmentName: String,
        private val isSelected: Boolean
    ) : AnAction(environmentName) {
        
        override fun actionPerformed(e: AnActionEvent) {
            val project = e.project ?: return
            val service = EnvironmentConfigurationService.getInstance(project)
            service.applyEnvironment(environmentName)
        }
        
        override fun update(e: AnActionEvent) {
            super.update(e)
            templatePresentation.putClientProperty("selected", isSelected)
        }
    }
    
    private class ConfigureAction : AnAction("Configure Environments...") {
        override fun actionPerformed(e: AnActionEvent) {
            val project = e.project ?: return
            EnvironmentConfigDialog(project).show()
        }
    }
}
