package com.jraska.module.graph.assertion

import com.jraska.module.graph.DependencyGraph
import com.jraska.module.graph.Parse
import org.gradle.api.GradleException

class OnlyAllowedAssert(
  private val allowedDependencies: Array<String>,
  private val aliasMap: Map<String, String> = emptyMap(),
) : GraphAssert {
  override fun assert(dependencyGraph: DependencyGraph) {
    val matchers = allowedDependencies.map { Parse.matcher(it) }

    val disallowedDependencies = dependencyGraph.dependencyPairs()
      .map { aliasMap.mapAlias(it) }
      .filterNot { dependency -> matchers.any { it.matches(dependency.pairToAssert()) } }
      .map { it.assertDisplayText() }

    if (disallowedDependencies.isNotEmpty()) {
      val allowedRules = allowedDependencies.joinToString(", ") { "'$it'" }
      throw GradleException("$disallowedDependencies not allowed by any of [$allowedRules]")
    }
  }
}

fun Map<String, String>.mapAlias(dependencyPair: Pair<String, String>): ModuleDependency {
  val fromAlias = this[dependencyPair.first]
  val toAlias = this[dependencyPair.second]

  return ModuleDependency(dependencyPair, fromAlias, toAlias)
}
