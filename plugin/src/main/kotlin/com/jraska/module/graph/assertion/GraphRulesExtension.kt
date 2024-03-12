package com.jraska.module.graph.assertion

open class GraphRulesExtension {
  var maxHeight: Int = 0

  /**
   * each restriction in format "regexp -X> regexp" e.g.: ":feature-[a-z]* -X> :forbidden-lib"
   */
  var restricted = emptySet<String>()

  /**
   * each allowance in format "regexp -> regexp" e.g.: ":feature-[a-z]* -> :forbidden-lib"
   */
  var allowed = emptySet<String>()
  var configurations: Set<String> = Api.API_IMPLEMENTATION_CONFIGURATIONS
  var assertOnAnyBuild: Boolean = false

  var outputFormat: OutputFormat = OutputFormat.GRAPHVIZ

  /**
   * When set, graph will be stored to this file in [outputFormat].

   */
  var outputFilePath: String? = null

  internal fun shouldAssertHeight() = maxHeight > 0

  internal fun shouldAssertRestricted() = restricted.isNotEmpty()

  internal fun shouldAssertAllowed() = allowed.isNotEmpty()
}
