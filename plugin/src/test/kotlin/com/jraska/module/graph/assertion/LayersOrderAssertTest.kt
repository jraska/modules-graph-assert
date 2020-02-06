package com.jraska.module.graph.assertion

import com.jraska.module.graph.DependencyGraph
import com.jraska.module.graph.ModuleNameRegexMatcher
import com.jraska.module.graph.Parse
import org.gradle.api.GradleException
import org.junit.Test
import java.util.function.Predicate

class LayersOrderAssertTest {
  @Test
  fun passesOnCorrectTree() {
    val dependencyGraph = testGraph()

    LayersOrderAssert(
      arrayOf("feature\\S*", "lib\\S*", "core\\S*").toMatchers(),
      setOf(Parse.matcher("lib -> feature2"))
    ).assert(dependencyGraph)
  }

  @Test(expected = GradleException::class)
  fun failsOnUnknownLayer() {
    val dependencyGraph = testGraph()

    LayersOrderAssert(arrayOf("feature\\S*", "lib\\S*", "corex\\S*").toMatchers()).assert(dependencyGraph)
  }

  @Test(expected = GradleException::class)
  fun failsOnWrongLayerOrder() {
    val dependencyGraph = testGraph()

    LayersOrderAssert(arrayOf("lib\\S*", "feature\\S*").toMatchers()).assert(dependencyGraph)
  }

  @Test
  fun passesOnLibLayer() {
    val dependencyGraph = testGraph()

    LayersOrderAssert(arrayOf("lib\\S*").toMatchers()).assert(dependencyGraph)
  }

  @Test
  fun passesOnCoreLayer() {
    val dependencyGraph = testGraph()

    LayersOrderAssert(
      arrayOf("core\\S*").toMatchers(),
      setOf(Parse.matcher("core-android -> core"))
    ).assert(dependencyGraph)
  }

  @Test(expected = GradleException::class)
  fun failsOnFeatureLayer() {
    LayersOrderAssert(arrayOf("feature\\S*").toMatchers()).assert(DependencyGraph.create("feature" to "feature2"))
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

  private fun Array<String>.toMatchers(): Array<Predicate<String>> {
    return map { ModuleNameRegexMatcher(it.toRegex()) }.toTypedArray()
  }
}
