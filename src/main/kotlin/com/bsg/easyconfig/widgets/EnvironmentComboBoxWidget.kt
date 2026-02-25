package com.bsg.easyconfig.widgets

import com.bsg.easyconfig.services.EnvironmentConfigurationService
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.Presentation
import com.intellij.openapi.actionSystem.ex.CustomComponentAction
import com.intellij.openapi.actionSystem.impl.ActionButton
import com.intellij.openapi.project.Project
import com.intellij.ui.components.JBLabel
import java.awt.BorderLayout
import java.awt.Dimension
import javax.swing.*

class EnvironmentComboBoxWidget(private val project: Project) : CustomComponentAction {
    
    private val comboBox = JComboBox<String>()
    private val panel = JPanel(BorderLayout())
    
    init {
        comboBox.preferredSize = Dimension(150, 30)
        comboBox.addActionListener {
            val selected = comboBox.selectedItem as? String
            if (selected != null && selected.isNotEmpty()) {
                val service = EnvironmentConfigurationService.getInstance(project)
                service.applyEnvironment(selected)
            }
        }
        
        panel.add(JBLabel("Env: "), BorderLayout.WEST)
        panel.add(comboBox, BorderLayout.CENTER)
    }
    
    override fun createCustomComponent(presentation: Presentation, place: String): JComponent {
        return panel
    }
    
    fun updateComboBox() {
        val service = EnvironmentConfigurationService.getInstance(project)
        val environments = service.getEnvironments()
        val selectedEnv = service.getSelectedEnvironment()
        
        comboBox.removeAllItems()
        
        if (environments.isEmpty()) {
            comboBox.addItem("(No environments)")
            comboBox.isEnabled = false
        } else {
            environments.forEach { env ->
                comboBox.addItem(env.name)
            }
            comboBox.isEnabled = true
            
            // Seleccionar el entorno actual
            if (selectedEnv.isNotEmpty()) {
                comboBox.selectedItem = selectedEnv
            }
        }
    }
    
    override fun updateCustomComponent(component: JComponent, presentation: Presentation) {
        updateComboBox()
    }
}
