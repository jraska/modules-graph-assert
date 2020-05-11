package com.jraska.module.graph

import org.junit.Test

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

  @Test
  fun groupsMatchingIsSupported() {
    val groupingRegex = ":features:(\\S*):\\S* -> :features:\\1:\\S*"

    val matcher = Parse.matcher(groupingRegex)

    assert(matcher.matches(":features:X:Y" to ":features:X:Z"))
    assert(matcher.matches(":features:data:Y" to ":features:data:Z"))
    assert(!matcher.matches(":features:data:Y" to ":features:dat:Z"))
  }

  @Test
  fun groupsMatchingIsSupportedForRestricted() {
    val groupingRegex = ":features:(\\S*):(\\S*) -X> :features:\\1:\\2:\\1"

    val restrictiveMatcher = Parse.restrictiveMatcher(groupingRegex)

    assert(restrictiveMatcher.matches(":features:X:Y" to ":features:X:Y:X"))
    assert(restrictiveMatcher.matches(":features:data:core" to ":features:data:core:data"))
    assert(!restrictiveMatcher.matches(":features:X:Y" to ":features:X:Z"))
    assert(!restrictiveMatcher.matches(":features:data:Y" to ":features:dat:Z"))
  }

  @Test(expected = IllegalArgumentException::class)
  fun failsOnWrongDefinition() {
    Parse.restrictiveMatcher(":feature -> :lib")
  }
}
