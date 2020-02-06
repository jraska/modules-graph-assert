package com.jraska.module.graph

import java.util.regex.Pattern

object RulesParse {
  const val NO_DEPENDENCY_SIGN_DIVIDER = " -X> "

  private val DEPENDENCY_FORMAT = Pattern.compile("([^\\s]+) -> ([^\\s]+)") // :feature -> :other-feature

  fun parse(dependencyText: String): Pair<String, String> {
    val matcher = DEPENDENCY_FORMAT.matcher(dependencyText)

    if (!matcher.find()) {
      throw IllegalArgumentException("$dependencyText is not in \"${DEPENDENCY_FORMAT.pattern()}\" format.")
    }

    return matcher.group(1) to matcher.group(2)
  }

  fun parseMatcher(matcherText: String): RegexpDependencyMatcher {
    if (!matcherText.contains(NO_DEPENDENCY_SIGN_DIVIDER)) {
      throw IllegalArgumentException("Incorrect format. Expected: 'regexp${NO_DEPENDENCY_SIGN_DIVIDER}regexp', found $matcherText")
    }

    val regexes = matcherText.split(NO_DEPENDENCY_SIGN_DIVIDER, limit = 2).map { it.toRegex() }
    val parent = regexes[0]
    val child = regexes[1]

    return RegexpDependencyMatcher(parent, child)
  }
}
