package com.jraska.module.graph.assertion

import org.gradle.api.Project
import org.gradle.api.artifacts.ProjectDependency
import org.gradle.api.provider.SetProperty
import org.gradle.api.services.BuildService
import org.gradle.api.services.BuildServiceParameters
import java.util.concurrent.ConcurrentHashMap

interface DependencyCollectorParameters : BuildServiceParameters {
  /**
   * The dependency configurations to analyze (e.g., "api", "implementation", "testImplementation").
   * Defaults to ["api", "implementation"] if not specified.
   */
  val configurations: SetProperty<String>
}

/**
 * Build service for collecting dependency information across all projects.
 * This is required for Gradle's isolated projects feature, where projects
 * cannot directly access configurations of other projects.
 */
abstract class DependencyCollectorService : BuildService<DependencyCollectorParameters> {

  private val projectDependencies = ConcurrentHashMap<String, List<String>>()
  private val projectAliases = ConcurrentHashMap<String, String>()

  fun getConfigurations(): Set<String> = parameters.configurations.get()

  fun registerProjectDependencies(projectName: String, dependencies: List<String>) {
    projectDependencies[projectName] = dependencies
  }

  fun registerProjectAlias(projectName: String, alias: String) {
    projectAliases[projectName] = alias
  }

  fun getAllProjectDependencies(): List<Pair<String, List<String>>> =
    projectDependencies.toList()

  fun getAllProjectAliases(): Map<String, String> =
    projectAliases.toMap()
}

/**
 * Collects dependencies from a single project (the one this is called on).
 * This avoids cross-project configuration access.
 */
fun Project.collectOwnDependencies(configurationsToLook: Set<String>): List<String> =
  configurations
    .filter { configurationsToLook.contains(it.name) }
    .flatMap { configuration ->
      configuration.dependencies
        .filterIsInstance<ProjectDependency>()
        .map { project(it.path) }
    }
    .map(Project::moduleDisplayName)
