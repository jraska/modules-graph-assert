package com.jraska.module.graph.assertion

import com.jraska.module.graph.DependencyGraph
import com.jraska.module.graph.DependencyMatcher
import org.gradle.api.GradleException

class LayersOrderAssert(
  private val layersFromTheTop: Array<String>,
  private val excludedForCheck: Collection<DependencyMatcher> = emptySet()
) : GraphAssert {

  override fun assert(dependencyGraph: DependencyGraph) {
    verifyAllLayersHaveModule(dependencyGraph)

    val againstLayerDependencies = dependencyGraph.dependencyPairs()
      .filter { isRestrictedCrossLayerDependency(it) }
      .filterNot { dependency -> excludedForCheck.any {it.matches(dependency)} }

    if (againstLayerDependencies.isNotEmpty()) {
      throw GradleException(buildErrorMessage(againstLayerDependencies))
    }
  }

  private fun buildErrorMessage(againstLayerDependencies: List<Pair<String, String>>): String {
    val errorsMessage = againstLayerDependencies.joinToString("\n") { " Module '${it.first}' cannot depend on '${it.second}'." }

    return "Direction of layers '${layersDependencyString()}' violated. The violating dependencies are: \n$errorsMessage"
  }

  private fun layersDependencyString(): String {
    return layersFromTheTop.joinToString(" -> ") // for example: ":feature -> :lib -> :core"
  }

  private fun verifyAllLayersHaveModule(modulesTree: DependencyGraph) {
    val nodes = modulesTree.nodes()

    for (layerPrefix in layersFromTheTop) {
      nodes.find { it.key.startsWith(layerPrefix) }
        ?: throw GradleException("There is no module, which belongs to layer '$layerPrefix'")
    }
  }

  private fun isRestrictedCrossLayerDependency(dependency: Pair<String, String>): Boolean {
    val higherLayerIndex = layerIndex(dependency.first) ?: return false
    val lowerLayerIndex = layerIndex(dependency.second) ?: return false

    return higherLayerIndex >= lowerLayerIndex // for == : "Dependencies within '$layerPrefix' are not allowed
  }

  private fun layerIndex(moduleName: String): Int? {
    for ((index, layerPrefix) in layersFromTheTop.withIndex()) {
      if (moduleName.startsWith(layerPrefix)) {
        return index
      }
    }

    return null
  }
}
