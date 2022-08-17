package com.jraska.module.graph.assertion

open class GraphRulesExtension {
  var maxHeight: Int = 0

  /**
   * each restriction in format "regexp -X> regexp" e.g.: ":feature-[a-z]* -X> :forbidden-lib"
   */
  var restricted =
    emptyArray<String>()

  /**
   * Whitelist a dependency to override a restriction.
   * Useful for refactoring phase, where one or a more dependency exist which requires time to adhere to newly defined restrictions
   * each whitelist in format "regexp -> regexp" e.g.: ":feature-[a-z]* -> :allowed-lib"
   */
  var whitelist =
    emptyArray<String>() // each whitelist in format "regexp -> regexp" e.g.: ":feature-[a-z]* -> :allowed-lib"
  var allowed =
    emptyArray<String>() // each allowance in format "regexp -> regexp" e.g.: ":feature-[a-z]* -> :forbidden-lib"
  var configurations: Set<String> = Api.API_IMPLEMENTATON_CONFIGURATIONS
}
