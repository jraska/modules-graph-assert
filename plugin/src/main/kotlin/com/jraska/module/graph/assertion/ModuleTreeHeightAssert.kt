package com.jraska.module.graph.assertion

import com.jraska.module.graph.DependencyGraph
import org.gradle.api.GradleException

class ModuleTreeHeightAssert(
  private val moduleName: String,
  private val maxHeight: Int
) {
  fun assert(dependencyGraph: DependencyGraph) {
    val height = dependencyGraph.heightOf(moduleName)
    if (height > maxHeight) {
      val longestPath = dependencyGraph.longestPath(moduleName)
      throw GradleException("Module $moduleName is allowed to have maximum height of $maxHeight, but has $height, problematic dependencies: ${longestPath.pathString()}")
    }
  }
}
