package com.jraska.module.graph.assertion

import com.jraska.module.graph.DependencyGraph
import com.jraska.module.graph.DependencyMatcher
import com.jraska.module.graph.Parse
import org.gradle.api.GradleException

class RestrictedDependenciesAssert(
  private val errorMatchers: Array<String>,
  private val aliasMap: Map<String, String> = emptyMap(),
  private val whitelists: Array<String> = emptyArray()
) : GraphAssert {
  override fun assert(dependencyGraph: DependencyGraph) {
    val matchers = errorMatchers.map { Parse.restrictiveMatcher(it) }
    val whitelistMatchers = whitelists.map { Parse.matcher(it) }


    val failedDependencies = dependencyGraph.dependencyPairs()
      .map { aliasMap.mapAlias(it) }
      .filter { dependency ->
        whitelistMatchers.none { it.matches(dependency.pairToAssert()) }
      }
      .map { dependency ->
        val violations = matchers.filter { it.matches(dependency.pairToAssert()) }.toList()
        dependency to violations
      }.filter { it.second.isNotEmpty() }

    if (failedDependencies.isNotEmpty()) {
      throw GradleException(buildErrorMessage(failedDependencies))
    }
  }

  private fun buildErrorMessage(failedDependencies: List<Pair<ModuleDependency, List<DependencyMatcher>>>): String {
    return failedDependencies.map {
      val violatedRules = it.second.map { "'$it'" }.joinToString(", ")
      "Dependency '${it.first.assertDisplayText()} violates: $violatedRules"
    }.joinToString("\n")
  }
}
