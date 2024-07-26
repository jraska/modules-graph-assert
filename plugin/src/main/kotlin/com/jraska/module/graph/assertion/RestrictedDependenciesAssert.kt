package com.jraska.module.graph.assertion

import com.jraska.module.graph.DependencyGraph
import com.jraska.module.graph.Parse
import com.jraska.module.graph.RegexpDependencyMatcher
import org.gradle.api.GradleException

class RestrictedDependenciesAssert(
  private val errorMatchers: Array<String>,
  private val aliasMap: Map<String, String> = emptyMap()
) : GraphAssert {
  override fun assert(dependencyGraph: DependencyGraph) {
    val matchers = errorMatchers.map { Parse.restrictiveMatcher(it) }

    val failedDependencies = dependencyGraph.dependencyPairs()
      .map { aliasMap.mapAlias(it) }
      .map { dependency ->
        val violations = matchers.filter { it.matches(dependency.pairToAssert()) }.toList()
        dependency to violations
      }.filter { it.second.isNotEmpty() }

    if (failedDependencies.isNotEmpty()) {
      throw GradleException(buildErrorMessage(failedDependencies))
    }
  }

  private fun buildErrorMessage(failedDependencies: List<Pair<ModuleDependency, List<RegexpDependencyMatcher>>>): String {
    return failedDependencies.map {
      val violatedRules = it.second.map { "'$it'" }.joinToString(", ")
      "Dependency '${it.first.assertDisplayText()} violates: $violatedRules"
    }.joinToString("\n")
  }
}
