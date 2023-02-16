package com.jraska.module.graph.assertion.tasks

import com.jraska.module.graph.DependencyGraph
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

open class GenerateModulesGraphStatisticsTask : DefaultTask() {
  @Input
  lateinit var dependencyGraph: DependencyGraph.SerializableGraph

  @TaskAction
  fun run() {
    val dependencyGraph = DependencyGraph.create(dependencyGraph)
    println(dependencyGraph.statistics())
  }
}
