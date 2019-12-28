package com.jraska.module.graph.assertion.tasks

import com.jraska.module.graph.assertion.GradleDependencyGraphFactory
import com.jraska.module.graph.assertion.ModuleTreeHeightAssert
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

open class AssertModuleTreeHeightTask : DefaultTask() {
  @Input
  lateinit var moduleName: String

  @Input
  var maxHeight: Int = 0

  @TaskAction
  fun run() {
    val modulesGraph = GradleDependencyGraphFactory.create(project)

    ModuleTreeHeightAssert(moduleName, maxHeight).assert(modulesGraph)
  }
}
