package com.jraska.module.graph.assertion.tasks

import com.jraska.module.graph.DependencyGraph
import com.jraska.module.graph.assertion.GraphAssert
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

open class AssertGraphTask : DefaultTask() {
  @Input
  lateinit var assertion: GraphAssert

  @Input
  lateinit var dependencyGraph: DependencyGraph.SerializableGraph

  @TaskAction
  fun run() {
    val modulesTree = DependencyGraph.create(dependencyGraph)

    assertion.assert(modulesTree)
  }
}
