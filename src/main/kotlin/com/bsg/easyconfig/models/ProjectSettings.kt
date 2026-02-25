package com.bsg.easyconfig.models

import com.intellij.util.xmlb.annotations.XCollection

/**
 * Settings for the entire project  
 */
data class ProjectSettings(
    var targetFilePath: String = "",
    var selectedEnvironment: String = "",
    @XCollection
    var environments: MutableList<EnvironmentConfig> = mutableListOf()
) {
    fun getEnvironment(name: String): EnvironmentConfig? {
        return environments.find { it.name == name }
    }
    
    fun addOrUpdateEnvironment(environment: EnvironmentConfig) {
        val index = environments.indexOfFirst { it.name == environment.name }
        if (index >= 0) {
            environments[index] = environment
        } else {
            environments.add(environment)
        }
    }
    
    fun removeEnvironment(name: String) {
        environments.removeIf { it.name == name }
    }
}
