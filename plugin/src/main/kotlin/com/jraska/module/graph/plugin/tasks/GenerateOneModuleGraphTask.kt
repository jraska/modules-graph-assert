package com.jraska.module.graph.plugin.tasks

import com.jraska.module.graph.plugin.GradleDependencyGraphFactory
import com.jraska.module.graph.GraphvizWriter
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

open class GenerateOneModuleGraphTask : DefaultTask() {
  @Input
  lateinit var moduleName: String

  @TaskAction
  fun run() {
    val allModulesTree = GradleDependencyGraphFactory.create(project)
    val moduleTree = allModulesTree.subTree(moduleName)

    println(moduleTree.statistics())
    println(GraphvizWriter.toGraphviz(moduleTree))
  }
}
