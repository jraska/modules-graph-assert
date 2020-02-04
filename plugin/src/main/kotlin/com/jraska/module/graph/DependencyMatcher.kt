package com.jraska.module.graph

interface DependencyMatcher {
  fun matches(dependency: Pair<String, String>): Boolean
}
