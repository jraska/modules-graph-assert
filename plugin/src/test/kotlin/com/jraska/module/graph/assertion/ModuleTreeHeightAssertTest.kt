package com.jraska.module.graph.assertion

import com.jraska.module.graph.DependencyGraph
import org.gradle.api.GradleException
import org.junit.Test

class ModuleTreeHeightAssertTest {
  @Test
  fun passesOnCorrectTree() {
    val dependencyGraph = testGraph()

    ModuleTreeHeightAssert("app", 3).assert(dependencyGraph)
  }

  @Test(expected = GradleException::class)
  fun failsOnUnknownLayer() {
    val dependencyGraph = testGraph()

    ModuleTreeHeightAssert("app", 2).assert(dependencyGraph)
  }

  @Test(expected = NoSuchElementException::class)
  fun failsOnUnknownModule() {
    val dependencyGraph = testGraph()

    ModuleTreeHeightAssert("appx", 2).assert(dependencyGraph)
  }

  private fun testGraph(): DependencyGraph {
    return DependencyGraph.create(
      listOf(
        "app" to "feature",
        "app" to "lib",
        "feature" to "lib",
        "lib" to "core"
      )
    )
  }
}
