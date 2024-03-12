package com.jraska.module.graph.assertion

import com.jraska.module.graph.DependencyGraph
import org.gradle.api.Project
import org.gradle.api.artifacts.ProjectDependency

object GradleDependencyGraphFactory {
  fun create(
    project: Project,
    configurationsToLook: Set<String>,
  ): DependencyGraph {
    val modulesWithDependencies = project.listAllDependencies(configurationsToLook)
    val dependencies =
      modulesWithDependencies.flatMap { module ->
        module.second.map { module.first to it }
      }

    val moduleDisplayName = project.moduleDisplayName()
    if (dependencies.isEmpty()) {
      return DependencyGraph.createSingular(moduleDisplayName)
    }

    val fullDependencyGraph = DependencyGraph.create(dependencies)

    if (project == project.rootProject) {
      return fullDependencyGraph
    }

    modulesWithDependencies.find { it.first == moduleDisplayName && it.second.isNotEmpty() }
      ?: return DependencyGraph.createSingular(moduleDisplayName)

    return fullDependencyGraph.subTree(moduleDisplayName)
  }

  private fun Project.listAllDependencies(configurationsToLook: Set<String>): List<Pair<String, List<String>>> {
    return (rootProject.subprojects + rootProject)
      .map { project ->
        project.moduleDisplayName() to
          project.configurations
            .filter { configurationsToLook.contains(it.name) }
            .flatMap { configuration ->
              configuration.dependencies.filterIsInstance(ProjectDependency::class.java)
                .map { it.dependencyProject }
            }
            .map { it.moduleDisplayName() }
      }
  }
}
