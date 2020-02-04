package com.jraska.module.graph

import org.junit.Test
import java.lang.IllegalArgumentException

class GraphParseTest {
  @Test
  fun parsesProperly() {
    val dependency = GraphParse.parse(":one -> two")

    assert(dependency == ":one" to "two")
  }

  @Test(expected = IllegalArgumentException::class)
  fun failsOnWrongFormat() {
    GraphParse.parse(":one - > two")
  }

  @Test(expected = IllegalArgumentException::class)
  fun failsOnEmptyString() {
    GraphParse.parse("")
  }
}
