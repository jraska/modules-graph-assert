package com.jraska.module.graph.assertion

open class GraphRulesExtension {
  var appModuleName = ":app"
  var maxHeight: Int = 0
  var moduleLayersFromTheTop = emptyArray<String>()
  var restrictInLayerDependencies = emptyArray<String>()
}
