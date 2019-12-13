package com.jraska.module.graph.plugin

import com.jraska.module.graph.plugin.tasks.AssertLayersOrderTask
import com.jraska.module.graph.plugin.tasks.AssertModuleTreeHeightTask
import com.jraska.module.graph.plugin.tasks.AssertNoInLayerDependencies
import com.jraska.module.graph.plugin.tasks.GenerateModulesGraphTask
import com.jraska.module.graph.plugin.Api.Tasks
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.language.base.plugins.LifecycleBasePlugin.VERIFICATION_GROUP
import java.util.Locale

@Suppress("unused", "UnstableApiUsage") // Used as plugin
class ModuleGraphAssertionsPlugin : Plugin<Project> {

  override fun apply(project: Project) {
    val graphRules = project.extensions.create(GraphRulesExtension::class.java, Api.EXTENSION_ROOT, GraphRulesExtension::class.java)

    project.afterEvaluate {
      addModulesAssertions(project, graphRules)
    }
  }

  private fun addModulesAssertions(project: Project, graphRules: GraphRulesExtension) {
    project.addModuleGraphGeneration()

    val allAssertionsTask = project.tasks.create(Tasks.ASSERT_ALL)
    allAssertionsTask.group = VERIFICATION_GROUP
    project.tasks.find { it.name == Api.CHECK_TASK }?.dependsOn(allAssertionsTask)

    project.addMaxHeightTasks(graphRules).forEach { allAssertionsTask.dependsOn(it) }
    project.addModuleLayersTasks(graphRules).forEach { allAssertionsTask.dependsOn(it) }
    project.addInLayerDependencyTasks(graphRules).forEach { allAssertionsTask.dependsOn(it) }
  }

  private fun Project.addModuleGraphGeneration() {
    tasks.create(Tasks.GENERATE_GRAPHVIZ, GenerateModulesGraphTask::class.java)
  }

  private fun Project.addMaxHeightTasks(graphRules: GraphRulesExtension): List<Task> {
    if (graphRules.maxHeight <= 0) {
      return emptyList()
    }

    val task = tasks.create(Tasks.ASSERT_MAX_HEIGHT, AssertModuleTreeHeightTask::class.java)
    task.maxHeight = graphRules.maxHeight
    task.moduleName = graphRules.appModuleName
    task.group = VERIFICATION_GROUP

    return listOf(task)
  }

  private fun Project.addModuleLayersTasks(graphRules: GraphRulesExtension): List<Task> {
    if (graphRules.moduleLayersFromTheTop.isEmpty()) {
      return emptyList()
    }

    val task = tasks.create(Tasks.ASSERT_LAYER_ORDER, AssertLayersOrderTask::class.java)
    task.layersFromTheTop = graphRules.moduleLayersFromTheTop
    task.group = VERIFICATION_GROUP

    return listOf(task)
  }

  private fun Project.addInLayerDependencyTasks(graphRules: GraphRulesExtension): List<Task> {
    return graphRules.restrinctInLayerDependencies.map { layerPrefix ->
      val taskNameSuffix = layerPrefix.replace(":", "").capitalizeFirst()
      val task = tasks.create("${Tasks.ASSERT_NO_IN_LAYER_PREFIX}$taskNameSuffix", AssertNoInLayerDependencies::class.java)
      task.layerPrefix = layerPrefix
      task.group = VERIFICATION_GROUP

      return@map task
    }
  }

  private fun String.capitalizeFirst(): String {
    return this.substring(0, 1).toUpperCase(Locale.US).plus(this.substring(1))
  }
}
