package com.jraska.module.graph.assertion.tasks

import com.jraska.module.graph.assertion.GradleDependencyGraphFactory
import com.jraska.module.graph.assertion.LayersOrderAssert
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

open class AssertLayersOrderTask : DefaultTask() {
  @Input
  lateinit var layersFromTheTop: Array<String>

  @Input
  lateinit var excludedForCheck: Set<Pair<String, String>>

  @TaskAction
  fun run() {
    val modulesGraph = GradleDependencyGraphFactory.create(project)

    LayersOrderAssert(layersFromTheTop, excludedForCheck).assert(modulesGraph)
  }
}
