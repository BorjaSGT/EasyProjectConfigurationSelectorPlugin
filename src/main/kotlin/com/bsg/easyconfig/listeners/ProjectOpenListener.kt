package com.bsg.easyconfig.listeners

import com.bsg.easyconfig.services.EnvironmentConfigurationService
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import java.io.File

class ProjectOpenListener : ProjectActivity {

    private val LOG = Logger.getInstance(ProjectOpenListener::class.java)

    override suspend fun execute(project: Project) {
        LOG.info("Project opened: ${project.name}")
        detectAndSetEnvironment(project)
    }

    private fun detectAndSetEnvironment(project: Project) {
        try {
            val service = EnvironmentConfigurationService.getInstance(project)
            val targetFilePath = service.getTargetFile()

            if (targetFilePath.isEmpty()) {
                LOG.info("No target file configured, skipping auto-detection")
                return
            }

            val projectBasePath = project.basePath ?: return
            val targetFile = File(projectBasePath, targetFilePath)

            if (!targetFile.exists()) {
                LOG.warn("Target file does not exist: ${targetFile.absolutePath}")
                return
            }

            val fileContent = targetFile.readText()
            val environments = service.getEnvironments()

            if (environments.isEmpty()) {
                LOG.info("No environments configured, skipping auto-detection")
                return
            }

            // Find environment matching current file content
            val matchingEnv = environments.find { env ->
                env.content.trim() == fileContent.trim()
            }

            if (matchingEnv != null) {
                LOG.info("Auto-detected environment: ${matchingEnv.name}")
                service.setSelectedEnvironment(matchingEnv.name)
            } else {
                LOG.info("No matching environment found for current file content")
                // If no exact match, try partial match (first lines)
                val fileLines = fileContent.lines().take(5).joinToString("\n")
                val partialMatch = environments.find { env ->
                    val envLines = env.content.lines().take(5).joinToString("\n")
                    envLines.trim() == fileLines.trim()
                }

                if (partialMatch != null) {
                    LOG.info("Auto-detected environment by partial match: ${partialMatch.name}")
                    service.setSelectedEnvironment(partialMatch.name)
                }
            }
        } catch (e: Exception) {
            LOG.error("Error during environment auto-detection", e)
        }
    }
}
