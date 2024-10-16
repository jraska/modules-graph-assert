package com.jraska.module.graph.assertion

open class GraphRulesExtension {
  var maxHeight: Int = 0
  var restricted = emptyArray<String>() // each restriction in format "regexp -X> regexp" e.g.: ":feature-[a-z]* -X> :forbidden-lib"
  var allowed = emptyArray<String>() // each allowance in format "regexp -> regexp" e.g.: ":feature-[a-z]* -> :forbidden-lib"
  var allowedViolations = emptyMap<String, List<String>>() // each allowed violation in the format ["feature-[a-z]*" : [forbidden-lib1, forbidden-lib2]]
  var configurations: Set<String> = Api.API_IMPLEMENTATION_CONFIGURATIONS
  var assertOnAnyBuild: Boolean = false

  internal fun shouldAssertHeight() = maxHeight > 0

  internal fun shouldAssertRestricted() = restricted.isNotEmpty()

  internal fun shouldAssertAllowed() = allowed.isNotEmpty()
}
