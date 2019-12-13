package com.jraska.module.graph.plugin.tasks

import com.jraska.module.graph.plugin.GradleDependencyGraphFactory
import com.jraska.module.graph.GraphvizWriter
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

open class GenerateModulesGraphTask : DefaultTask() {
  @Input
  var layers: Array<String> = emptyArray()

  @TaskAction
  fun run() {
    val allModulesTree = GradleDependencyGraphFactory.create(project)

    println(allModulesTree.statistics())
    println(GraphvizWriter.toGraphviz(allModulesTree, layers.toSet()))
  }
}
