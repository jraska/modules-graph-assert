package com.jraska.module.graph.assertion.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

open class GenerateModulesGraphStatisticsTask : DefaultTask() {
  @Input
  lateinit var configurationsToLook: Set<String>

  @TaskAction
  fun run() {
    val dependencyGraph = project.createDependencyGraph(configurationsToLook)
    println(dependencyGraph.statistics())
  }
}
