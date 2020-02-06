package com.jraska.module.graph.assertion.tasks

import com.jraska.module.graph.assertion.GradleDependencyGraphFactory
import com.jraska.module.graph.assertion.GraphAssert
import com.jraska.module.graph.assertion.UserDefinedRulesAssert
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

open class AssertGraphTask : DefaultTask() {

  lateinit var assertion: GraphAssert

  @TaskAction
  fun run() {
    val modulesTree = GradleDependencyGraphFactory.create(project)

    assertion.assert(modulesTree)
  }
}
