package com.jraska.module.graph.assertion

import com.jraska.module.graph.DependencyGraph
import org.junit.Test

class UserDefinedRulesAssertTest {
  @Test
  fun passesWithNoMatchingMatchers() {
    val dependencyGraph = testGraph()

    UserDefinedRulesAssert(emptySet()).assert(dependencyGraph)
  }

  private fun testGraph(): DependencyGraph {
    return DependencyGraph.create(
      "app" to "feature",
      "app" to "feature2",
      "app" to "lib",
      "feature" to "lib",
      "feature" to "lib2",
      "feature" to "feature2",
      "lib" to "core",
      "lib2" to "core-android",
      "core-android" to "core"
    )
  }
}
