package com.jraska.module.graph.assertion

import com.jraska.module.graph.DependencyMatcher
import com.jraska.module.graph.RulesParse
import com.jraska.module.graph.assertion.Api.Tasks
import com.jraska.module.graph.assertion.tasks.*
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.language.base.plugins.LifecycleBasePlugin.CHECK_TASK_NAME
import org.gradle.language.base.plugins.LifecycleBasePlugin.VERIFICATION_GROUP
import java.util.*

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

    val task = tasks.create(Tasks.ASSERT_MAX_HEIGHT, AssertModuleTreeHeightTask::class.java)
    task.maxHeight = graphRules.maxHeight
    task.moduleName = graphRules.appModuleName
    task.group = VERIFICATION_GROUP

    return task
  }

  private fun Project.addModuleLayersTask(graphRules: GraphRulesExtension): Task? {
    if (graphRules.moduleLayersFromTheTop.isEmpty()) {
      return null
    }

    val task = tasks.create(Tasks.ASSERT_LAYER_ORDER, AssertLayersOrderTask::class.java)
    task.layersFromTheTop = graphRules.moduleLayersFromTheTop
    task.excludedForCheck = graphRules.excludedFromLayers()
    task.group = VERIFICATION_GROUP

    return task
  }


  private fun Project.addModuleUserRuleTask(graphRules: GraphRulesExtension): Task? {
    if (graphRules.restricted.isEmpty()) {
      return null
    }

    val task = tasks.create(Tasks.ASSERT_USER_RULES, AssertUserDefinedRulesTask::class.java)
    task.matchers = graphRules.userRulesMatchers()
    task.group = VERIFICATION_GROUP

    return task
  }

  private fun String.capitalizeFirst(): String {
    return this.substring(0, 1).toUpperCase(Locale.US).plus(this.substring(1))
  }

  private fun GraphRulesExtension.excludedFromLayers(): Set<Pair<String, String>> {
    return excludeFromLayersCheck.map { RulesParse.parse(it) }.toSet()
  }

  private fun GraphRulesExtension.userRulesMatchers(): Collection<DependencyMatcher> {
    return restricted.map { RulesParse.parseMatcher(it) }
  }
}
