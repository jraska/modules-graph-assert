package com.jraska.module.graph

import org.junit.Test

class GraphvizWriterTest {

  val EXPECTED_OUTPUT = """digraph G {
"app" -> "feature" [color=red style=bold]
"app" -> "feature-about"
"app" -> "lib"
"feature" -> "lib" [color=red style=bold]
"feature-about" -> "lib"
"feature-about" -> "core"
"lib" -> "core" [color=red style=bold]
}"""

  @Test
  fun testPrintsProperly() {
    val graphvizText = GraphvizWriter.toGraphviz(testGraph())

    assert(graphvizText == EXPECTED_OUTPUT)
  }

  private fun testGraph(): DependencyGraph {
    return DependencyGraph.create(
      "app" to "feature",
      "app" to "feature-about",
      "feature-about" to "lib",
      "feature-about" to "core",
      "app" to "lib",
      "feature" to "lib",
      "lib" to "core"
    )
  }
}
