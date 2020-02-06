package com.jraska.module.graph

import org.junit.Test
import java.lang.IllegalArgumentException

class ParseTest {
  @Test
  fun parsesProperly() {
    val matcher = Parse.matcher(":one -> two")

    assert(matcher.matches(":one" to "two"))
  }

  @Test(expected = IllegalArgumentException::class)
  fun failsOnWrongFormat() {
    Parse.matcher(":one - > two")
  }

  @Test(expected = IllegalArgumentException::class)
  fun failsOnEmptyString() {
    Parse.matcher("")
  }

  @Test
  fun parsesMatcherProperly() {
    val matcher = Parse.restrictiveMatcher(":feature:[a-zA-Z]* -X> :lib")

    assert(matcher.matches(":feature:about" to ":lib"))
    assert(matcher.matches(":feature:aboutX" to ":lib"))
    assert(!matcher.matches(":feature:aboutX" to ":libx"))
    assert(!matcher.matches(":feature-about" to ":lib"))
  }

  @Test(expected = IllegalArgumentException::class)
  fun failsOnWrongDefinition() {
    Parse.restrictiveMatcher(":feature -> :lib")
  }
}
