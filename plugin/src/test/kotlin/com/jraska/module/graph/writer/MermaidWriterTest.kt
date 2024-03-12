package com.jraska.module.graph.writer

import com.jraska.module.graph.DependencyGraph
import org.junit.Test

class MermaidWriterTest {
  @Test
  fun testPrintsProperly() {
    val graphText = MermaidWriter.toGraph(testGraph())

    assert(graphText == EXPECTED_OUTPUT)
  }

  @Test
  fun testPrintsProperlyWithAliases() {
    val aliases =
      mapOf(
        "app" to "App",
        "lib" to "Api",
        "feature-about" to "Implementation",
      )

    val graphText = MermaidWriter.toGraph(testGraph(), aliases)

    assert(graphText == EXPECTED_OUTPUT_WITH_ALISASES)
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
    private const val EXPECTED_OUTPUT = """```mermaid
graph LR;
app(app) --> feature(feature);
app(app) --> feature-about(feature-about);
app(app) --> lib(lib);
feature(feature) --> lib(lib);
feature-about(feature-about) --> lib(lib);
feature-about(feature-about) --> core(core);
lib(lib) --> core(core);

linkStyle 0 stroke-width:2px,stroke:red;
linkStyle 3 stroke-width:2px,stroke:red;
linkStyle 6 stroke-width:2px,stroke:red;
linkStyle default stroke-width:1px;
```
"""

    private const val EXPECTED_OUTPUT_WITH_ALISASES = """```mermaid
graph LR;
app(App) --> feature(feature);
app(App) --> feature-about(Implementation);
app(App) --> lib(Api);
feature(feature) --> lib(Api);
feature-about(Implementation) --> lib(Api);
feature-about(Implementation) --> core(core);
lib(Api) --> core(core);

linkStyle 0 stroke-width:2px,stroke:red;
linkStyle 3 stroke-width:2px,stroke:red;
linkStyle 6 stroke-width:2px,stroke:red;
linkStyle default stroke-width:1px;
```
"""
  }
}
