package com.jraska.module.graph.assertion

import com.jraska.module.graph.DependencyMatcher
import com.jraska.module.graph.ModuleNameRegexMatcher
import com.jraska.module.graph.Parse
import com.jraska.module.graph.assertion.Api.Tasks
import com.jraska.module.graph.assertion.tasks.AssertGraphTask
import com.jraska.module.graph.assertion.tasks.GenerateModulesGraphTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.language.base.plugins.LifecycleBasePlugin.CHECK_TASK_NAME
import org.gradle.language.base.plugins.LifecycleBasePlugin.VERIFICATION_GROUP
import java.util.*
import java.util.function.Predicate

@Suppress("unused", "UnstableApiUsage") // Used as plugin
class ModuleGraphAssertionsPlugin : Plugin<Project> {

  override fun apply(project: Project) {
    val graphRules = project.extensions.create(GraphRulesExtension::class.java, Api.EXTENSION_ROOT, GraphRulesExtension::class.java)

    project.afterEvaluate {
      addModulesAssertions(project, graphRules)
    }
  }

  internal fun addModulesAssertions(project: Project, graphRules: GraphRulesExtension) {
    project.addModuleGraphGeneration()

    val allAssertionsTask = project.tasks.create(Tasks.ASSERT_ALL)
    allAssertionsTask.group = VERIFICATION_GROUP
    project.tasks.find { it.name == CHECK_TASK_NAME }?.dependsOn(allAssertionsTask)

    project.addMaxHeightTask(graphRules)?.also { allAssertionsTask.dependsOn(it) }
    project.addModuleLayersTask(graphRules)?.also { allAssertionsTask.dependsOn(it) }
    project.addModuleUserRuleTask(graphRules)?.also { allAssertionsTask.dependsOn(it) }
  }

  private fun Project.addModuleGraphGeneration() {
    tasks.create(Tasks.GENERATE_GRAPHVIZ, GenerateModulesGraphTask::class.java)
  }

  private fun Project.addMaxHeightTask(graphRules: GraphRulesExtension): Task? {
    if (graphRules.maxHeight <= 0) {
      return null
    }

    val task = tasks.create(Tasks.ASSERT_MAX_HEIGHT, AssertGraphTask::class.java)
    task.assertion = ModuleTreeHeightAssert(graphRules.appModuleName, graphRules.maxHeight)
    task.group = VERIFICATION_GROUP

    return task
  }

  private fun Project.addModuleLayersTask(graphRules: GraphRulesExtension): Task? {
    if (graphRules.moduleLayers.isEmpty()) {
      return null
    }

    val task = tasks.create(Tasks.ASSERT_LAYER_ORDER, AssertGraphTask::class.java)
    task.assertion = LayersOrderAssert(graphRules.layerMatchers(), graphRules.excludedFromLayers())
    task.group = VERIFICATION_GROUP

    return task
  }


  private fun Project.addModuleUserRuleTask(graphRules: GraphRulesExtension): Task? {
    if (graphRules.restricted.isEmpty()) {
      return null
    }

    val task = tasks.create(Tasks.ASSERT_USER_RULES, AssertGraphTask::class.java)
    task.assertion = UserDefinedRulesAssert(graphRules.userRulesMatchers())
    task.group = VERIFICATION_GROUP

    return task
  }

  private fun String.capitalizeFirst(): String {
    return this.substring(0, 1).toUpperCase(Locale.US).plus(this.substring(1))
  }

  private fun GraphRulesExtension.excludedFromLayers(): Collection<DependencyMatcher> {
    return excludeLayersCheck.map { Parse.matcher(it) }
  }

  private fun GraphRulesExtension.userRulesMatchers(): Collection<DependencyMatcher> {
    return restricted.map { Parse.restrictiveMatcher(it) }
  }

  private fun GraphRulesExtension.layerMatchers(): Array<Predicate<String>> {
    return moduleLayers.map { ModuleNameRegexMatcher(it.toRegex()) }.toTypedArray()
  }
}
