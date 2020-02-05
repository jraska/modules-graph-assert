package com.jraska.module.graph.assertion

import com.jraska.module.graph.DependencyGraph
import com.jraska.module.graph.DependencyMatcher
import org.gradle.api.GradleException

class UserDefinedRulesAssert(
  private val errorMatchers: Collection<DependencyMatcher>
) {
  fun assert(dependencyGraph: DependencyGraph) {

    val failedDependencies = dependencyGraph.dependencyPairs()
      .map { dependency ->
        val violations = errorMatchers.filter { it.matches(dependency) }.toList()
        dependency to violations
      }.filter { it.second.isNotEmpty() }

    if (failedDependencies.isNotEmpty()) {
      throw GradleException(buildErrorMessage(failedDependencies)) //todo
    }
  }

  private fun buildErrorMessage(failedDependencies: List<Pair<Pair<String, String>, List<DependencyMatcher>>>): String {
    return failedDependencies.map {
      val violatedRules = it.second.map { "'$it'" }.joinToString(", ")
      "Dependency '${it.first.first}' -> '${it.first.second}' violates: $violatedRules"
    }.joinToString("\n")
  }
}
