package com.jraska.module.graph.assertion

import com.jraska.module.graph.DependencyGraph
import com.jraska.module.graph.DependencyMatcher
import com.jraska.module.graph.Parse
import org.gradle.api.GradleException

class OnlyAllowedAssert(
  private val allowedDependencies: Array<String>
) : GraphAssert {
  override fun assert(dependencyGraph: DependencyGraph) {
    val matchers = allowedDependencies.map { Parse.matcher(it) }

    val disallowedDependencies = dependencyGraph.dependencyPairs()
      .filterNot { dependency -> matchers.any { it.matches(dependency) } }

    if (disallowedDependencies.isNotEmpty()) {
      throw GradleException("$disallowedDependencies not allowed")
    }
  }
}
