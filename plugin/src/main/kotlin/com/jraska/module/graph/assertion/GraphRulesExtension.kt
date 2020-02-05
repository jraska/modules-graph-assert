package com.jraska.module.graph.assertion

open class GraphRulesExtension {
  var appModuleName = ":app"
  var maxHeight: Int = 0
  var moduleLayersFromTheTop = emptyArray<String>()
  var restrictInLayerDependencies = emptyArray<String>()
  var excludeFromLayersCheck = emptyArray<String>() // each exclusion in format ":module -> :other-module"
  var restricted = emptyArray<String>() // each restriction in format "regexp -X> regexp" e.g.: ":feature-[a-z]* -X> :forbidden-lib"
}
