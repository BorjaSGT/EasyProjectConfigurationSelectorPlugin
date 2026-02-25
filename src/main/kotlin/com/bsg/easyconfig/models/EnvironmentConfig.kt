package com.bsg.easyconfig.models

/**
 * Represents a single environment configuration
 */
data class EnvironmentConfig(
    var name: String = "",
    var content: String = ""
) {
    companion object {
        fun empty() = EnvironmentConfig("", "")
    }
}
