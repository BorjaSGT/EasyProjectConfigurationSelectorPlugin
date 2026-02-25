package com.bsg.easyconfig.ui

import com.bsg.easyconfig.models.EnvironmentConfig
import com.bsg.easyconfig.services.EnvironmentConfigurationService
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.ui.components.JBList
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.ToolbarDecorator
import java.awt.BorderLayout
import java.awt.Dimension
import javax.swing.*

class EnvironmentConfigDialog(private val project: Project) : DialogWrapper(project) {
    
    private val service = EnvironmentConfigurationService.getInstance(project)
    private val targetFileField = TextFieldWithBrowseButton()
    private val environmentListModel = DefaultListModel<String>()
    private val environmentList = JBList(environmentListModel)
    private val environments = mutableMapOf<String, EnvironmentConfig>()
    
    init {
        title = "Configure Environments"
        init()
        loadCurrentSettings()
    }
    
    private fun loadCurrentSettings() {
        targetFileField.text = service.getTargetFile()
        
        service.getEnvironments().forEach { env ->
            environments[env.name] = env
            environmentListModel.addElement(env.name)
        }
    }
    
    override fun createCenterPanel(): JComponent {
        val panel = JPanel(BorderLayout())
        panel.preferredSize = Dimension(600, 400)
        
        // Top panel - Target file selection
        val topPanel = JPanel(BorderLayout())
        topPanel.border = BorderFactory.createTitledBorder("Target Configuration File")
        
        targetFileField.addBrowseFolderListener(
            project,
            FileChooserDescriptorFactory.singleFile()
                .withTitle("Select Configuration File")
                .withDescription("Choose the file to be managed by environment switching")
        )
        
        topPanel.add(targetFileField, BorderLayout.CENTER)
        panel.add(topPanel, BorderLayout.NORTH)
        
        // Center panel - Environment list
        val centerPanel = JPanel(BorderLayout())
        centerPanel.border = BorderFactory.createTitledBorder("Environments")
        
        val decorator = ToolbarDecorator.createDecorator(environmentList)
            .setAddAction { addEnvironment() }
            .setRemoveAction { removeEnvironment() }
            .setEditAction { editEnvironment() }
            .createPanel()
        
        centerPanel.add(decorator, BorderLayout.CENTER)
        panel.add(centerPanel, BorderLayout.CENTER)
        
        return panel
    }
    
    private fun addEnvironment() {
        val dialog = EnvironmentEditDialog(project, null)
        if (dialog.showAndGet()) {
            val newEnv = dialog.getEnvironment()
            if (newEnv.name.isNotEmpty() && !environments.containsKey(newEnv.name)) {
                environments[newEnv.name] = newEnv
                environmentListModel.addElement(newEnv.name)
            }
        }
    }
    
    private fun removeEnvironment() {
        val selectedName = environmentList.selectedValue
        if (selectedName != null) {
            environments.remove(selectedName)
            environmentListModel.removeElement(selectedName)
        }
    }
    
    private fun editEnvironment() {
        val selectedName = environmentList.selectedValue
        if (selectedName != null) {
            val currentEnv = environments[selectedName]
            val dialog = EnvironmentEditDialog(project, currentEnv)
            if (dialog.showAndGet()) {
                val updatedEnv = dialog.getEnvironment()
                
                // If name changed, update the map
                if (updatedEnv.name != selectedName) {
                    environments.remove(selectedName)
                    environmentListModel.removeElement(selectedName)
                }
                
                environments[updatedEnv.name] = updatedEnv
                if (updatedEnv.name != selectedName) {
                    environmentListModel.addElement(updatedEnv.name)
                }
            }
        }
    }
    
    override fun doOKAction() {
        // Save target file
        val targetPath = targetFileField.text
        if (targetPath.isNotEmpty()) {
            // Convert absolute path to relative path from project base
            val projectBasePath = project.basePath ?: ""
            val relativePath = if (targetPath.startsWith(projectBasePath)) {
                targetPath.substring(projectBasePath.length).trimStart('/')
            } else {
                targetPath
            }
            service.setTargetFile(relativePath)
        }
        
        // Clear existing environments and add new ones
        service.getSettings().environments.clear()
        environments.values.forEach { env ->
            service.addEnvironment(env)
        }
        
        super.doOKAction()
    }
}
