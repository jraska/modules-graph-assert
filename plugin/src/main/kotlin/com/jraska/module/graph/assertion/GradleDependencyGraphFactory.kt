package com.jraska.module.graph.assertion

import com.jraska.module.graph.DependencyGraph
import org.gradle.api.Project
import org.gradle.api.internal.artifacts.dependencies.DefaultProjectDependency

object GradleDependencyGraphFactory {

  fun create(project: Project): DependencyGraph {
    val dependencies = project.listDependencyPairs()

    val fullDependencyGraph = DependencyGraph.create(dependencies)

    return if (project == project.rootProject) {
      fullDependencyGraph
    } else {
      fullDependencyGraph.subTree(project.moduleDisplayName())
    }
  }

  private fun Project.listDependencyPairs(): List<Pair<String, String>> {
    val configurationToLook = setOf("implementation", "api")
    return rootProject.subprojects
      .flatMap { project ->
        project.configurations
          .filter { configurationToLook.contains(it.name) }
          .flatMap { configuration ->
            configuration.dependencies.filterIsInstance(DefaultProjectDependency::class.java)
              .map { it.dependencyProject }
          }
          .map { project.moduleDisplayName() to it.moduleDisplayName() }
      }
  }
}
