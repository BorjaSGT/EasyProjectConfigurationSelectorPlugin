package com.bsg.easyconfig.services

import com.bsg.easyconfig.models.EnvironmentConfig
import com.bsg.easyconfig.models.ProjectSettings
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.StoragePathMacros
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.util.xmlb.XmlSerializerUtil
import java.io.File

/**
 * Service to manage environment configurations for the project
 * State is persisted in .idea/easyEnvironmentConfigurationsPlugin.xml
 */
@State(
    name = "EasyEnvironmentConfigurationsPlugin",
    storages = [Storage("easyEnvironmentConfigurationsPlugin.xml")]
)
class EnvironmentConfigurationService(private val project: Project) : 
    PersistentStateComponent<ProjectSettings> {
    
    private var settings = ProjectSettings()
    
    override fun getState(): ProjectSettings {
        return settings
    }
    
    override fun loadState(state: ProjectSettings) {
        settings = state
    }
    
    fun getSettings(): ProjectSettings {
        return settings
    }
    
    fun setTargetFile(filePath: String) {
        settings.targetFilePath = filePath
    }
    
    fun getTargetFile(): String {
        return settings.targetFilePath
    }
    
    fun addEnvironment(environment: EnvironmentConfig) {
        settings.addOrUpdateEnvironment(environment)
    }
    
    fun removeEnvironment(name: String) {
        settings.removeEnvironment(name)
    }
    
    fun clearEnvironments() {
        settings.environments.clear()
    }
    
    fun getEnvironments(): List<EnvironmentConfig> {
        return settings.environments.toList()
    }
    
    fun getSelectedEnvironment(): String {
        return settings.selectedEnvironment
    }
    
    fun setSelectedEnvironment(name: String) {
        settings.selectedEnvironment = name
    }
    
    /**
     * Apply the configuration for the selected environment
     */
    fun applyEnvironment(environmentName: String): Boolean {
        val environment = settings.getEnvironment(environmentName) ?: return false
        
        if (settings.targetFilePath.isEmpty()) {
            return false
        }
        
        return ApplicationManager.getApplication().runWriteAction<Boolean> {
            try {
                val projectBasePath = project.basePath ?: return@runWriteAction false
                val targetFile = File(projectBasePath, settings.targetFilePath)
                
                if (!targetFile.exists()) {
                    targetFile.parentFile?.mkdirs()
                }
                
                targetFile.writeText(environment.content)
                
                // Refresh the virtual file system
                val vFile = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(targetFile)
                vFile?.refresh(false, false)
                
                // Reload the document in the editor if it's open
                vFile?.let { 
                    FileDocumentManager.getInstance().reloadFromDisk(
                        FileDocumentManager.getInstance().getDocument(it) ?: return@let
                    )
                }
                
                settings.selectedEnvironment = environmentName
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }
    
    companion object {
        fun getInstance(project: Project): EnvironmentConfigurationService {
            return project.getService(EnvironmentConfigurationService::class.java)
        }
    }
}
