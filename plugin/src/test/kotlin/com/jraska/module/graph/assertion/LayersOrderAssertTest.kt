package com.jraska.module.graph.assertion

import com.jraska.module.graph.DependencyGraph
import com.jraska.module.graph.Parse
import org.gradle.api.GradleException
import org.junit.Test

class LayersOrderAssertTest {
  @Test
  fun passesOnCorrectTree() {
    val dependencyGraph = testGraph()

    LayersOrderAssert(
      arrayOf("feature", "lib", "core"),
      setOf(Parse.matcher("lib -> feature2"))
    ).assert(dependencyGraph)
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

  @Test
  fun passesOnLibLayer() {
    val dependencyGraph = testGraph()

    LayersOrderAssert(arrayOf("lib")).assert(dependencyGraph)
  }

  @Test
  fun passesOnCoreLayer() {
    val dependencyGraph = testGraph()

    LayersOrderAssert(
      arrayOf("core"),
      setOf(Parse.matcher("core-android -> core"))
    ).assert(dependencyGraph)
  }

  @Test(expected = GradleException::class)
  fun failsOnFeatureLayer() {
    LayersOrderAssert(arrayOf("feature")).assert(DependencyGraph.create("feature" to "feature2"))
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
