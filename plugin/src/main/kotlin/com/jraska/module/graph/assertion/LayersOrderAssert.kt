package com.jraska.module.graph.assertion

import com.jraska.module.graph.DependencyGraph
import com.jraska.module.graph.DependencyMatcher
import org.gradle.api.GradleException
import java.util.function.Predicate

class LayersOrderAssert(
  private val layersFromTheTop: Array<Predicate<String>>,
  private val excludedForCheck: Collection<DependencyMatcher> = emptySet()
) : GraphAssert {

  override fun assert(dependencyGraph: DependencyGraph) {
    verifyAllLayersHaveModule(dependencyGraph)

    val againstLayerDependencies = dependencyGraph.dependencyPairs()
      .filter { isRestrictedCrossLayerDependency(it) }
      .filterNot { dependency -> excludedForCheck.any { it.matches(dependency) } }

    if (againstLayerDependencies.isNotEmpty()) {
      throw GradleException(buildErrorMessage(againstLayerDependencies))
    }
  }

  private fun buildErrorMessage(againstLayerDependencies: List<Pair<String, String>>): String {
    val errorsMessage = againstLayerDependencies.joinToString("\n") { " Module '${it.first}' cannot depend on '${it.second}'." }

    return "Direction of layers ${layersDependencyString()} violated. The violating dependencies are: \n$errorsMessage"
  }

  private fun layersDependencyString(): String {
    return layersFromTheTop.joinToString(" -> ") // for example: ":feature -> :lib -> :core"
  }

  private fun verifyAllLayersHaveModule(modulesTree: DependencyGraph) {
    val nodes = modulesTree.nodes()

    for (layerMatcher in layersFromTheTop) {
      nodes.find { layerMatcher.test(it.key) }
        ?: throw GradleException("There is no module, which belongs to layer described: $layerMatcher")
    }
  }

  private fun isRestrictedCrossLayerDependency(dependency: Pair<String, String>): Boolean {
    val higherLayerIndex = layerIndex(dependency.first) ?: return false
    val lowerLayerIndex = layerIndex(dependency.second) ?: return false

    return higherLayerIndex >= lowerLayerIndex // for == : "Dependencies within '$layerPrefix' are not allowed
  }

  private fun layerIndex(moduleName: String): Int? {
    for ((index, layerMatcher) in layersFromTheTop.withIndex()) {
      if (layerMatcher.test(moduleName)) {
        return index
      }
    }

    return null
  }
}
