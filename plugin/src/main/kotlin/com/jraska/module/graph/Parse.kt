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

  private fun matcher(
    matcherText: String,
    divider: String,
  ): RegexpDependencyMatcher {
    require(matcherText.contains(divider)) {
      "Incorrect format. Expected: 'regexp${divider}regexp', found $matcherText"
    }

    return RegexpDependencyMatcher(matcherText.toRegex(), divider)
  }
}
