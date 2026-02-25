package com.bsg.easyconfig.actions

import com.bsg.easyconfig.services.EnvironmentConfigurationService
import com.bsg.easyconfig.ui.EnvironmentConfigDialog
import com.intellij.openapi.actionSystem.ActionGroup
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.Separator
import com.intellij.openapi.diagnostic.Logger

class EnvironmentSelectorGroupAction : ActionGroup() {
    
    private val LOG = Logger.getInstance(EnvironmentSelectorGroupAction::class.java)
    
    override fun getChildren(e: AnActionEvent?): Array<AnAction> {
        e ?: return emptyArray()
        
        val project = e.project
        if (project == null) {
            LOG.warn("No project in event")
            return emptyArray()
        }
        
        val service = EnvironmentConfigurationService.getInstance(project)
        val environments = service.getEnvironments()
        
        LOG.info("Getting children: ${environments.size} environments")
        
        val actions = mutableListOf<AnAction>()
        
        if (environments.isEmpty()) {
            LOG.warn("No environments found")
            actions.add(ConfigureEnvironmentsAction())
        } else {
            environments.forEach { env ->
                LOG.info("Adding action for: ${env.name}")
                actions.add(SelectEnvironmentAction(env.name))
            }
            actions.add(Separator.getInstance())
            actions.add(ConfigureEnvironmentsAction())
        }
        
        return actions.toTypedArray()
    }
    
    override fun update(e: AnActionEvent) {
        val project = e.project
        e.presentation.isEnabledAndVisible = project != null
        
        if (project != null) {
            val service = EnvironmentConfigurationService.getInstance(project)
            val selectedEnv = service.getSelectedEnvironment()
            e.presentation.text = if (selectedEnv.isEmpty()) {
                "Environment"
            } else {
                "Env: $selectedEnv"
            }
        } else {
            e.presentation.text = "Environment"
        }
    }
    
    private class SelectEnvironmentAction(private val environmentName: String) : AnAction(environmentName) {
        override fun actionPerformed(e: AnActionEvent) {
            val project = e.project ?: return
            val service = EnvironmentConfigurationService.getInstance(project)
            service.applyEnvironment(environmentName)
        }
    }
}
