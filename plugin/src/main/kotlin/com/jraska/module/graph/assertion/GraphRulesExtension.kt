package com.jraska.module.graph.assertion

open class GraphRulesExtension {
  var maxHeight: Int = 0
  var restricted = emptyArray<String>() // each restriction in format "regexp -X> regexp" e.g.: ":feature-[a-z]* -X> :forbidden-lib"
  var allowed = emptyArray<String>() // each allowance in format "regexp -> regexp" e.g.: ":feature-[a-z]* -> :forbidden-lib"
  var configurations: Set<String> = Api.API_IMPLEMENTATON_CONFIGURATIONS
}
