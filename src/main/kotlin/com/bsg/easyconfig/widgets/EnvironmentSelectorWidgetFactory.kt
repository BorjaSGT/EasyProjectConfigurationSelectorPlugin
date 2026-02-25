package com.bsg.easyconfig.widgets

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.StatusBar
import com.intellij.openapi.wm.StatusBarWidget
import com.intellij.openapi.wm.StatusBarWidgetFactory

class EnvironmentSelectorWidgetFactory : StatusBarWidgetFactory {
    
    override fun getId(): String = EnvironmentSelectorWidget.ID
    
    override fun getDisplayName(): String = "Environment Selector"
    
    override fun isAvailable(project: Project): Boolean = true
    
    override fun createWidget(project: Project): StatusBarWidget {
        return EnvironmentSelectorWidget(project)
    }
    
    override fun disposeWidget(widget: StatusBarWidget) {
        widget.dispose()
    }
    
    override fun canBeEnabledOn(statusBar: StatusBar): Boolean = true
}
