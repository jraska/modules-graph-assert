package com.jraska.module.graph.assertion

import com.jraska.module.graph.DependencyGraph
import org.gradle.api.GradleException

class LayersOrderAssert(
  private val layersFromTheTop: Array<String>
) {

  fun assert(dependencyGraph: DependencyGraph) {
    verifyAllLayersHaveModule(dependencyGraph)

    val againstLayerDependencies = dependencyGraph.nodes()
      .flatMap { parent -> parent.dependsOn.map { dependency -> parent to dependency } }
      .filter { isRestrictedDependency(it) }

    if (againstLayerDependencies.isNotEmpty()) {
      throw GradleException(buildErrorMessage(againstLayerDependencies))
    }
  }

  private fun buildErrorMessage(againstLayerDependencies: List<Pair<DependencyGraph.Node, DependencyGraph.Node>>): String {
    val errorsMessage = againstLayerDependencies.joinToString("\n") { " Module '${it.first.key}' cannot depend on '${it.second.key}'." }

    return "Dependencies against direction of layers '${layersDependencyString()}' are not allowed. The violating dependencies are: \n$errorsMessage"
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

  private fun isRestrictedDependency(dependency: Pair<DependencyGraph.Node, DependencyGraph.Node>): Boolean {
    val higherLayerIndex = layerIndex(dependency.first.key) ?: return false
    val lowerLayerIndex = layerIndex(dependency.second.key) ?: return false

    return higherLayerIndex > lowerLayerIndex
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
