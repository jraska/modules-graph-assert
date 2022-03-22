package com.jraska.module.graph.assertion.tasks

import com.jraska.module.graph.assertion.report.ModuleGraphStatisticsReporter
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

open class GenerateModulesGraphStatisticsTask : DefaultTask() {
  @Input
  lateinit var configurationsToLook: Set<String>

  @TaskAction
  fun run() {
    val dependencyGraph = project.createDependencyGraphs(configurationsToLook)
    val statistics = dependencyGraph.map { it.statistics() }
    println(statistics)
    ModuleGraphStatisticsReporter.report(gradle = project.gradle, statistics = statistics)
  }
}
