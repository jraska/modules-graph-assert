package com.jraska.module.graph.assertion

import com.jraska.module.graph.DependencyGraph
import com.jraska.module.graph.Parse
import org.gradle.api.GradleException
import org.junit.Test

class OnlyAllowedAssertTest {
  @Test(expected = GradleException::class)
  fun failsWithNoMatchingMatchers() {
    val dependencyGraph = testGraph()

    OnlyAllowedAssert(emptySet()).assert(dependencyGraph)
  }

  @Test
  fun passesWhenAllAllowed() {
    val dependencyGraph = testGraph()

    OnlyAllowedAssert(setOf(Parse.matcher(".* -> .*"))).assert(dependencyGraph)
  }

  @Test
  fun passesWhenAllowed() {
    val dependencyGraph = testGraph()

    val allowedDependencies = setOf(
      "app -> .*",
      "feature[a-z]* -> lib[0-9]*",
      "feature[a-z]* -> api[0-9]*",
      "api[0-9]* -> lib",
    ).map { Parse.matcher(it) }

    OnlyAllowedAssert(allowedDependencies).assert(dependencyGraph)
  }

  @Test(expected = GradleException::class)
  fun failsWhenOneNotAllowed() {
    val dependencies = testGraph().dependencyPairs().toMutableList().apply { add("api" to "lib2") }
    val dependencyGraph = DependencyGraph.create(dependencies)

    val allowedDependencies = setOf(
      "app -> .*",
      "feature[a-z]* -> lib[0-9]*",
      "feature[a-z]* -> api[0-9]*",
      "api[0-9]* -> lib",
    ).map { Parse.matcher(it) }

    OnlyAllowedAssert(allowedDependencies).assert(dependencyGraph)
  }

  private fun testGraph(): DependencyGraph {
    return DependencyGraph.create(
      "app" to "feature",
      "app" to "feature2",
      "app" to "api",
      "feature" to "api",
      "feature" to "api2",
      "api" to "lib",
      "api2" to "lib",
    )
  }
}
