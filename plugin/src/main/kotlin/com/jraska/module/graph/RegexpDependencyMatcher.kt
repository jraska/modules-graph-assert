package com.jraska.module.graph

class RegexpDependencyMatcher(
  private val matchingRegex: Regex,
  private val divider: String,
) : DependencyMatcher {
  override fun matches(dependency: Pair<String, String>): Boolean {
    val dependencyToMatch = "${dependency.first}$divider${dependency.second}"
    return matchingRegex.matches(dependencyToMatch)
  }

  override fun toString(): String {
    return matchingRegex.toString()
  }
}
