package com.jraska.module.graph.assertion

import com.jraska.module.graph.DependencyGraph
import org.gradle.api.tasks.VerificationException
import java.io.Serializable

class ModuleTreeHeightAssert(
  private val moduleName: String?,
  private val maxHeight: Int
) : GraphAssert, Serializable {
  override fun assert(dependencyGraph: DependencyGraph) {
    if (moduleName == null) {
      assertWholeGraphHeight(dependencyGraph)
    } else {
      assertModuleHeight(dependencyGraph, moduleName)
    }
  }

  private fun assertModuleHeight(dependencyGraph: DependencyGraph, moduleName: String) {
    val height = dependencyGraph.heightOf(moduleName)
    if (height > maxHeight) {
      val longestPath = dependencyGraph.longestPath(moduleName)
      throw VerificationException("Module $moduleName is allowed to have maximum height of $maxHeight, but has $height, problematic dependencies: ${longestPath.pathString()}")
    }
  }

  private fun assertWholeGraphHeight(dependencyGraph: DependencyGraph) {
    val height = dependencyGraph.height()
    if (height > maxHeight) {
      val longestPath = dependencyGraph.longestPath()
      throw VerificationException("Module Graph is allowed to have maximum height of $maxHeight, but has $height, problematic dependencies: ${longestPath.pathString()}")
    }
  }
}
