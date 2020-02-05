package com.jraska.module.graph

class RegexDependencyMatcher(
  private val parentRegex: Regex,
  private val childRegex: Regex
) : DependencyMatcher {

  override fun matches(dependency: Pair<String, String>): Boolean {
    return dependency.first.matches(parentRegex)
      && dependency.second.matches(childRegex)
  }

  override fun toString(): String {
    return "$parentRegex ${GraphParse.NO_DEPENDENCY_SIGN} $childRegex"
  }
}
