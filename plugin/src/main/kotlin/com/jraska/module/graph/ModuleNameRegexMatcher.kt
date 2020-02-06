package com.jraska.module.graph

import java.util.function.Predicate

class ModuleNameRegexMatcher(private val regex: Regex) : Predicate<String> {
  override fun test(moduleName: String): Boolean {
    return regex.matches(moduleName)
  }

  override fun toString(): String {
    return "Regex '$regex'"
  }
}
