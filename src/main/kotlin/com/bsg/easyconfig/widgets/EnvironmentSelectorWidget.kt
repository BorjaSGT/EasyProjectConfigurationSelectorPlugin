package com.bsg.easyconfig.widgets

import com.bsg.easyconfig.services.EnvironmentConfigurationService
import com.bsg.easyconfig.ui.EnvironmentConfigDialog
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.openapi.ui.popup.PopupStep
import com.intellij.openapi.ui.popup.util.BaseListPopupStep
import com.intellij.openapi.wm.StatusBar
import com.intellij.openapi.wm.StatusBarWidget
import com.intellij.ui.ClickListener
import com.intellij.ui.awt.RelativePoint
import com.intellij.util.ui.UIUtil
import java.awt.event.MouseEvent
import javax.swing.JComponent
import javax.swing.JLabel

class EnvironmentSelectorWidget(private val project: Project) : StatusBarWidget, StatusBarWidget.TextPresentation {
    
    companion object {
        const val ID = "EnvironmentSelectorWidget"
    }
    
    private var myStatusBar: StatusBar? = null
    private val label = JLabel()
    
    init {
        object : ClickListener() {
            override fun onClick(event: MouseEvent, clickCount: Int): Boolean {
                showPopup(label)
                return true
            }
        }.installOn(label)
    }
    
    override fun ID(): String = ID
    
    override fun getPresentation(): StatusBarWidget.WidgetPresentation {
        return this
    }
    
    override fun install(statusBar: StatusBar) {
        myStatusBar = statusBar
    }
    
    override fun dispose() {
        myStatusBar = null
    }
    
    override fun getTooltipText(): String {
        return "Environment: ${getText()}"
    }
    
    override fun getText(): String {
        val service = EnvironmentConfigurationService.getInstance(project)
        val selected = service.getSelectedEnvironment()
        return if (selected.isEmpty()) "No Environment" else selected
    }
    
    override fun getAlignment(): Float {
        return 0.5f
    }
    
    @Deprecated("Deprecated in Java")
    override fun getClickConsumer(): Nothing? {
        return null
    }
    
    private fun showPopup(component: JComponent) {
        val service = EnvironmentConfigurationService.getInstance(project)
        val environments = service.getEnvironments()
        
        if (environments.isEmpty()) {
            EnvironmentConfigDialog(project).show()
            return
        }
        
        val items = environments.map { it.name }.toMutableList()
        items.add("Configure...")
        
        val popup = JBPopupFactory.getInstance().createListPopup(
            object : BaseListPopupStep<String>("Select Environment", items) {
                override fun onChosen(selectedValue: String?, finalChoice: Boolean): PopupStep<*>? {
                    if (selectedValue == "Configure...") {
                        EnvironmentConfigDialog(project).show()
                    } else if (selectedValue != null) {
                        service.applyEnvironment(selectedValue)
                        update()
                    }
                    return PopupStep.FINAL_CHOICE
                }
            }
        )
        
        popup.show(RelativePoint.getSouthOf(component))
    }
    
    fun update() {
        myStatusBar?.updateWidget(ID())
    }
}
