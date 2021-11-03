package com.jraska.module.graph.assertion

open class GraphRulesExtension {
  var maxHeight: Int = 0

  @Deprecated("Will be removed in version 2.0")
  var moduleLayers = emptyArray<String>()

  @Deprecated("Will be removed in version 2.0")
  var moduleLayersExclude = emptyArray<String>() // each exclusion in format ":module -> :other-module"

  var restricted = emptyArray<String>() // each restriction in format "regexp -X> regexp" e.g.: ":feature-[a-z]* -X> :forbidden-lib"
  var allowed = emptyArray<String>() // each allowance in format "regexp -X> regexp" e.g.: ":feature-[a-z]* -> :forbidden-lib"
  var configurations: Set<String> = Api.API_IMPLEMENTATON_CONFIGURATIONS
}
