package com.jraska.module.graph.assertion

import org.junit.Test

class ModuleDependencyTest {
  @Test
  fun displayTextWorksWithAliases() {
    val displayText = ModuleDependency(":feature" to ":core-api", "Impl", "Api").assertDisplayText()

    assert(displayText == """"Impl"(':feature') -> "Api"(':core-api')""")
  }

  @Test
  fun displayTextWorksWithOneAlias() {
    val displayText = ModuleDependency(":feature" to ":core-api", null, "Api").assertDisplayText()

    assert(displayText == """':feature' -> "Api"(':core-api')""")
  }

  @Test
  fun displayTextWorksWithNoAliases() {
    val displayText = ModuleDependency(":feature" to ":core-api", null, null).assertDisplayText()

    assert(displayText == "':feature' -> ':core-api'")
  }
}
