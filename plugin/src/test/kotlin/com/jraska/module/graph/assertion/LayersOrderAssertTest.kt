package com.jraska.module.graph.assertion

import com.jraska.module.graph.DependencyGraph
import org.gradle.api.GradleException
import org.junit.Test

class LayersOrderAssertTest {
  @Test
  fun passesOnCorrectTree() {
    val dependencyGraph = testGraph()

    LayersOrderAssert(arrayOf("feature", "lib", "core"), setOf("lib" to "feature2")).assert(dependencyGraph)
  }

  @Test(expected = GradleException::class)
  fun failsOnUnknownLayer() {
    val dependencyGraph = testGraph()

    LayersOrderAssert(arrayOf("feature", "lib", "corex")).assert(dependencyGraph)
  }

  @Test(expected = GradleException::class)
  fun failsOnWrongLayerOrder() {
    val dependencyGraph = testGraph()

    LayersOrderAssert(arrayOf("lib", "feature")).assert(dependencyGraph)
  }

  private fun testGraph(): DependencyGraph {
    return DependencyGraph.create(
      "app" to "feature",
      "app" to "lib",
      "feature2" to "core",
      "feature" to "lib",
      "lib" to "core",
      "lib" to "feature2"
    )
  }
}
