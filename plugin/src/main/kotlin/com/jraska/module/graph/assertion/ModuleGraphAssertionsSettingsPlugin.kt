package com.jraska.module.graph.assertion

import com.jraska.module.graph.assertion.GradleModuleAliasExtractor.extractOwnAlias
import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings

/**
 * Settings plugin that enables isolated projects compatibility.
 *
 * REQUIRED for isolated projects mode. Apply this in your settings.gradle(.kts):
 *
 * ```kotlin
 * plugins {
 *     id("com.jraska.module.graph.assertion.settings") version "2.10.0"
 * }
 * ```
 *
 * This plugin uses gradle.lifecycle.beforeProject to collect dependency information
 * from all projects in an isolated projects-compatible way. Each project registers
 * itself with the build service without cross-project access.
 */
class ModuleGraphAssertionsSettingsPlugin : Plugin<Settings> {

  companion object {
    const val SERVICE_NAME = "dependencyCollector"
  }

  override fun apply(settings: Settings) {
    settings.registerDependencyCollectorService()

    @Suppress("UnstableApiUsage")
    settings.gradle.lifecycle.beforeProject { project ->
      project.afterEvaluate {
        project.getDependencyCollectorService()?.let { service ->
          val configs = service.getConfigurations()
          val deps = project.collectOwnDependencies(configs)
          service.registerProjectDependencies(project.moduleDisplayName(), deps)

          project.extractOwnAlias()?.let { alias ->
            service.registerProjectAlias(project.moduleDisplayName(), alias)
          }
        }
      }
    }
  }
}
