package com.jraska.module.graph.assertion

import org.junit.Test

class DependencyToAssertTest {
  @Test
  fun displayTextWorksWithAliases() {
    val displayText = DependencyToAssert(":feature" to ":core-api", "Impl", "Api").displayText()

    assert(displayText == """"Impl"(':feature') -> "Api"(':core-api')""")
  }

  @Test
  fun displayTextWorksWithOneAlias() {
    val displayText = DependencyToAssert(":feature" to ":core-api", null, "Api").displayText()

    assert(displayText == """':feature' -> "Api"(':core-api')""")
  }

  @Test
  fun displayTextWorksWithNoAliases() {
    val displayText = DependencyToAssert(":feature" to ":core-api", null, null).displayText()

    assert(displayText == "':feature' -> ':core-api'")
  }
}
