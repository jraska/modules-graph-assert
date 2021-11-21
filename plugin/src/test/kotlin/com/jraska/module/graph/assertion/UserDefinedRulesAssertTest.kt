package com.jraska.module.graph.assertion

import com.jraska.module.graph.DependencyGraph
import com.jraska.module.graph.Parse
import org.gradle.api.GradleException
import org.junit.Test

class UserDefinedRulesAssertTest {
  @Test
  fun passesWithNoMatchingMatchers() {
    val dependencyGraph = testGraph()

    UserDefinedRulesAssert(emptyArray()).assert(dependencyGraph)
  }

  @Test(expected = GradleException::class)
  fun failsWhenFeatureCannotDependOnLib() {
    val dependencyGraph = testGraph()

    UserDefinedRulesAssert(arrayOf("feature -X> lib2")).assert(dependencyGraph)
  }

  @Test(expected = GradleException::class)
  fun failsWhenLibCannotDependOnAndroid() {
    val dependencyGraph = testGraph()

    UserDefinedRulesAssert(arrayOf("lib[0-9]* -X> [a-z]*-android")).assert(dependencyGraph)
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
      "lib" to "core-android",
      "lib2" to "core-android",
      "core-android" to "core"
    )
  }
}
