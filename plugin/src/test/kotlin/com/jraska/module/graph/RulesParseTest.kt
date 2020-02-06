package com.jraska.module.graph

import org.junit.Test
import java.lang.IllegalArgumentException

class RulesParseTest {
  @Test
  fun parsesProperly() {
    val dependency = RulesParse.parse(":one -> two")

    assert(dependency == ":one" to "two")
  }

  @Test(expected = IllegalArgumentException::class)
  fun failsOnWrongFormat() {
    RulesParse.parse(":one - > two")
  }

  @Test(expected = IllegalArgumentException::class)
  fun failsOnEmptyString() {
    RulesParse.parse("")
  }

  @Test
  fun parsesMatcherProperly() {
    val matcher = RulesParse.parseMatcher(":feature:[a-zA-Z]* -X> :lib")

    assert(matcher.matches(":feature:about" to ":lib"))
    assert(matcher.matches(":feature:aboutX" to ":lib"))
    assert(!matcher.matches(":feature:aboutX" to ":libx"))
    assert(!matcher.matches(":feature-about" to ":lib"))
  }

  @Test(expected = IllegalArgumentException::class)
  fun failsOnWrongDefinition() {
    RulesParse.parseMatcher(":feature -> :lib")
  }
}
