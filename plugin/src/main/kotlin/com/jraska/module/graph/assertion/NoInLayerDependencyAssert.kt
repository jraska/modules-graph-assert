package com.jraska.module.graph.assertion

import com.jraska.module.graph.DependencyGraph
import org.gradle.api.GradleException

class NoInLayerDependencyAssert(
  private val layerPrefix: String,
  private val excludedForCheck: Set<Pair<String, String>> = emptySet()
) {

  fun assert(modulesTree: DependencyGraph) {
    val inLayerDependencies = modulesTree.nodes()
      .filter { it.key.startsWith(layerPrefix) }
      .ifEmpty { throw GradleException("There are no modules with prefix $layerPrefix") }
      .flatMap { parent -> parent.dependsOn.map { dependency -> parent to dependency } }
      .filter { it.second.key.startsWith(layerPrefix) }
      .map { it.first.key to it.second.key }
      .filterNot { excludedForCheck.contains(it) }

    if (inLayerDependencies.isNotEmpty()) {
      throw GradleException(buildErrorMessage(inLayerDependencies))
    }
  }

  private fun buildErrorMessage(inLayerDependencies: List<Pair<String, String>>): String {
    val errorsMessage = inLayerDependencies.joinToString("\n") { " Module '${it.first}' cannot depend on '${it.second}'." }
    val suggestion = "Try using shared interface modules to break this dependency"

    return "Dependencies within '$layerPrefix' are not allowed. The not allowed dependencies are: \n$errorsMessage\n$suggestion"
  }
}
