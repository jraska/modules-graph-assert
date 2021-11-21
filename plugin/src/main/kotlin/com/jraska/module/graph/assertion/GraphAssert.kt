package com.jraska.module.graph.assertion

import com.jraska.module.graph.DependencyGraph
import java.io.Serializable

interface GraphAssert : Serializable {
  fun assert(dependencyGraph: DependencyGraph)
}
