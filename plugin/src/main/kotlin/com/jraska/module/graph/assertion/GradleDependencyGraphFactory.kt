package com.jraska.module.graph.assertion

import com.jraska.module.graph.DependencyGraph
import org.gradle.api.Project

object GradleDependencyGraphFactory {

  fun create(project: Project, service: DependencyCollectorService): DependencyGraph {
    val modulesWithDependencies = service.getAllProjectDependencies()
    val dependencies = modulesWithDependencies.flatMap { module ->
      module.second.map { module.first to it }
    }

    val moduleDisplayName = project.moduleDisplayName()
    if(dependencies.isEmpty()) {
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
}
