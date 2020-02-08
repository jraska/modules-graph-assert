package com.jraska.module.graph.assertion

open class GraphRulesExtension {
  var maxHeight: Int = 0
  var moduleLayers = emptyArray<String>()
  var excludeLayersCheck = emptyArray<String>() // each exclusion in format ":module -> :other-module"
  var restricted = emptyArray<String>() // each restriction in format "regexp -X> regexp" e.g.: ":feature-[a-z]* -X> :forbidden-lib"
}
