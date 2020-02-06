package com.jraska.module.graph

class RegexpDependencyMatcher(
  private val parentRegex: Regex,
  private val childRegex: Regex,
  private val divider: String
) : DependencyMatcher {

  override fun matches(dependency: Pair<String, String>): Boolean {
    return dependency.first.matches(parentRegex)
      && dependency.second.matches(childRegex)
  }

  override fun toString(): String {
    return "$parentRegex$divider$childRegex"
  }
}
