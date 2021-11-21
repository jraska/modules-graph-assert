package com.jraska.module.graph.assertion

import com.jraska.module.graph.DependencyGraph
import com.jraska.module.graph.DependencyMatcher
import com.jraska.module.graph.Parse
import org.gradle.api.GradleException

class UserDefinedRulesAssert(
  private val errorMatchers: Array<String>
) : GraphAssert {
  override fun assert(dependencyGraph: DependencyGraph) {
    val matchers = errorMatchers.map { Parse.restrictiveMatcher(it) }

    val failedDependencies = dependencyGraph.dependencyPairs()
      .map { dependency ->
        val violations = matchers.filter { it.matches(dependency) }.toList()
        dependency to violations
      }.filter { it.second.isNotEmpty() }

    if (failedDependencies.isNotEmpty()) {
      throw GradleException(buildErrorMessage(failedDependencies))
    }
  }

  private fun buildErrorMessage(failedDependencies: List<Pair<Pair<String, String>, List<DependencyMatcher>>>): String {
    return failedDependencies.map {
      val violatedRules = it.second.map { "'$it'" }.joinToString(", ")
      "Dependency '${it.first.first}' -> '${it.first.second}' violates: $violatedRules"
    }.joinToString("\n")
  }
}
