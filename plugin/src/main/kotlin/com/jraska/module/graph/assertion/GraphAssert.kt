package com.jraska.module.graph.assertion

import com.jraska.module.graph.DependencyGraph

interface GraphAssert {
  fun assert(dependencyGraph: DependencyGraph)
}
