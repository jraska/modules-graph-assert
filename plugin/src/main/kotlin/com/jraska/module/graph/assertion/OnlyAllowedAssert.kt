package com.jraska.module.graph.assertion

import com.jraska.module.graph.DependencyGraph
import com.jraska.module.graph.DependencyMatcher
import org.gradle.api.GradleException

class OnlyAllowedAssert(
  private val allowedDependencies: Collection<DependencyMatcher>
) : GraphAssert {
  override fun assert(dependencyGraph: DependencyGraph) {

    val disallowedDependencies = dependencyGraph.dependencyPairs()
      .filterNot { dependency -> allowedDependencies.any { it.matches(dependency) } }

    if (disallowedDependencies.isNotEmpty()) {
      throw GradleException("$disallowedDependencies not allowed")
    }
  }
}
