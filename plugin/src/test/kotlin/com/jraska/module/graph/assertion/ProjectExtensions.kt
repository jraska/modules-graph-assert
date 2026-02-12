@file:JvmName("ProjectTestExtensions")

package com.jraska.module.graph.assertion

import org.gradle.api.Project

fun Project.collectAndRegisterDependencies(configurations: Set<String>) {
  val dependencyCollectorService = getDependencyCollectorService()!!
    .apply { parameters.configurations.set(configurations) }
  (subprojects + this).forEach { project ->
    val deps = project.collectOwnDependencies(dependencyCollectorService.getConfigurations())
    dependencyCollectorService.registerProjectDependencies(project.moduleDisplayName(), deps)
  }
}
