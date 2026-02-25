package com.bsg.easyconfig.ui

import com.bsg.easyconfig.models.EnvironmentConfig
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.ValidationInfo
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.components.JBTextField
import java.awt.BorderLayout
import java.awt.Dimension
import javax.swing.*

class EnvironmentEditDialog(
    project: Project,
    private val existingEnvironment: EnvironmentConfig?
) : DialogWrapper(project) {
    
    private val nameField = JBTextField()
    private val contentArea = JTextArea()
    
    init {
        title = if (existingEnvironment == null) "Add Environment" else "Edit Environment"
        init()
        loadEnvironment()
    }
    
    private fun loadEnvironment() {
        existingEnvironment?.let {
            nameField.text = it.name
            contentArea.text = it.content
        }
    }
    
    override fun createCenterPanel(): JComponent {
        val panel = JPanel(BorderLayout(10, 10))
        panel.preferredSize = Dimension(500, 400)
        
        // Name field
        val namePanel = JPanel(BorderLayout())
        namePanel.add(JLabel("Environment Name:"), BorderLayout.WEST)
        namePanel.add(nameField, BorderLayout.CENTER)
        panel.add(namePanel, BorderLayout.NORTH)
        
        // Content area
        val contentPanel = JPanel(BorderLayout())
        contentPanel.border = BorderFactory.createTitledBorder("Configuration Content")
        
        contentArea.lineWrap = true
        contentArea.wrapStyleWord = true
        contentArea.font = java.awt.Font("Monospaced", java.awt.Font.PLAIN, 12)
        
        val scrollPane = JBScrollPane(contentArea)
        contentPanel.add(scrollPane, BorderLayout.CENTER)
        
        panel.add(contentPanel, BorderLayout.CENTER)
        
        return panel
    }
    
    fun getEnvironment(): EnvironmentConfig {
        return EnvironmentConfig(
            name = nameField.text.trim(),
            content = contentArea.text
        )
    }
    
    override fun doValidate(): ValidationInfo? {
        if (nameField.text.trim().isEmpty()) {
            return ValidationInfo("Environment name cannot be empty", nameField)
        }
        return null
    }
}
