package com.jraska.module.graph.writer

import com.jraska.module.graph.DependencyGraph
import org.junit.Test

class GraphvizWriterTest {
  @Test
  fun testPrintsProperly() {
    val graphvizText = GraphvizWriter.toGraph(testGraph())

    assert(graphvizText == EXPECTED_OUTPUT)
  }

  @Test
  fun testPrintsProperlyWithAliases() {
    val aliases =
      mapOf(
        "app" to "App",
        "lib" to "Api",
        "feature-about" to "Implementation",
      )

    val graphvizText = GraphvizWriter.toGraph(testGraph(), aliases)

    assert(graphvizText == EXPECTED_OUTPUT_WITH_ALISASES)
  }

  private fun testGraph(): DependencyGraph {
    return DependencyGraph.create(
      "app" to "feature",
      "app" to "feature-about",
      "feature-about" to "lib",
      "feature-about" to "core",
      "app" to "lib",
      "feature" to "lib",
      "lib" to "core",
    )
  }

  companion object {
    private const val EXPECTED_OUTPUT = """digraph G {
"app" -> "feature" [color=red style=bold]
"app" -> "feature-about"
"app" -> "lib"
"feature" -> "lib" [color=red style=bold]
"feature-about" -> "lib"
"feature-about" -> "core"
"lib" -> "core" [color=red style=bold]
}"""

    private const val EXPECTED_OUTPUT_WITH_ALISASES = """digraph G {
"app('App')" -> "feature" [color=red style=bold]
"app('App')" -> "feature-about('Implementation')"
"app('App')" -> "lib('Api')"
"feature" -> "lib('Api')" [color=red style=bold]
"feature-about('Implementation')" -> "lib('Api')"
"feature-about('Implementation')" -> "core"
"lib('Api')" -> "core" [color=red style=bold]
}"""
  }
}
