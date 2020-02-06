package com.jraska.module.graph

object Parse {
  const val NO_DEPENDENCY_SIGN_DIVIDER = " -X> "
  const val DEPENDENCY_SIGN_DIVIDER = " -> "

  fun matcher(dependencyText: String): RegexpDependencyMatcher {
    return matcher(dependencyText, DEPENDENCY_SIGN_DIVIDER)
  }

  fun restrictiveMatcher(matcherText: String): RegexpDependencyMatcher {
    return matcher(matcherText, NO_DEPENDENCY_SIGN_DIVIDER)
  }

  private fun matcher(matcherText: String, divider: String): RegexpDependencyMatcher {
    if (!matcherText.contains(divider)) {
      throw IllegalArgumentException("Incorrect format. Expected: 'regexp${divider}regexp', found $matcherText")
    }

    val regexes = matcherText.split(divider, limit = 2).map { it.toRegex() }
    val parent = regexes[0]
    val child = regexes[1]

    return RegexpDependencyMatcher(parent, child, divider)
  }
}
