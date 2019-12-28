package com.jraska.module.graph.assertion

import com.jraska.module.graph.DependencyGraph
import org.gradle.api.GradleException
import org.junit.Test

class NoInLayerDependencyAssertTest {

  @Test
  fun passesOnLibLayer() {
    val dependencyGraph = testGraph()

    NoInLayerDependencyAssert("lib").assert(dependencyGraph)
  }

  @Test
  fun passesOnCoreLayer() {
    val dependencyGraph = testGraph()

    NoInLayerDependencyAssert("core").assert(dependencyGraph)
  }

  @Test(expected = GradleException::class)
  fun failsOnFeatureLayer() {
    val dependencyGraph = testGraph()

    NoInLayerDependencyAssert("feature").assert(dependencyGraph)
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
      "lib2" to "core"
    )
  }
}
