package com.bsg.easyconfig.models

/**
 * Bean class for XML serialization (Plan B)
 * Standard Java Bean pattern without Kotlin data class
 */
class ProjectSettingsBean {
    var targetFilePath: String = ""
    var selectedEnvironment: String = ""
    var environments: MutableList<EnvironmentConfig> = mutableListOf()
    
    // Helper methods
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
    
    // For debugging
    override fun toString(): String {
        return "ProjectSettingsBean(targetFilePath='$targetFilePath', selectedEnvironment='$selectedEnvironment', environments=${environments.size} items)"
    }
}
