package com.jraska.module.graph.assertion

import com.jraska.module.graph.assertion.tasks.AssertGraphTask
import org.gradle.api.internal.project.DefaultProject
import org.gradle.api.plugins.JavaLibraryPlugin
import org.gradle.language.base.plugins.LifecycleBasePlugin.CHECK_TASK_NAME
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Test

class ModuleGraphAssertionsPluginTest {
  private lateinit var project: DefaultProject

  @Before
  fun setUp() {
    project = ProjectBuilder.builder().withName("app").build() as DefaultProject
    project.plugins.apply(JavaLibraryPlugin::class.java)
  }

  @Test
  fun testAddsOnlyOneTaskWhenApplied() {
    val checkDependsOnSize = project.tasks.findByName(CHECK_TASK_NAME)!!.dependsOn.size

    project.plugins.apply(ModuleGraphAssertionsPlugin::class.java)
    project.evaluate()

    assert(project.tasks.findByName(Api.Tasks.ASSERT_ALL) != null)
    assert(project.tasks.findByName(Api.Tasks.ASSERT_ALL)!!.dependsOn.isEmpty())
    assert(project.tasks.findByName(CHECK_TASK_NAME)!!.dependsOn.size == checkDependsOnSize + 1)
  }

  @Test
  fun testAddsOnlyOneTaskWhenApplied2() {
    val plugin = ModuleGraphAssertionsPlugin()

    val extension = GraphRulesExtension().apply {
      maxHeight = 3
      moduleLayers = arrayOf(":feature", ":lib", ":core")
      moduleLayersExclude = arrayOf(":feature-one -> :feature-two")
      restricted = arrayOf(":feature-one -X> :feature-two")
    }

    plugin.addModulesAssertions(project, extension)

    assert(project.tasks.findByName(Api.Tasks.ASSERT_ALL) != null)
    assert(project.tasks.findByName(Api.Tasks.ASSERT_ALL)!!.dependsOn.size == 3)

    setOf(
      project.tasks.findByName(Api.Tasks.ASSERT_MAX_HEIGHT) as AssertGraphTask,
      project.tasks.findByName(Api.Tasks.ASSERT_LAYER_ORDER) as AssertGraphTask,
      project.tasks.findByName(Api.Tasks.ASSERT_USER_RULES) as AssertGraphTask
    ).forEach {
      assert(it.configurationsToLook == Api.API_IMPLEMENTATON_CONFIGURATIONS)
    }
  }

  @Test
  fun testPrintGraphvizTextsIsAdded() {
    project.plugins.apply(ModuleGraphAssertionsPlugin::class.java)
    project.evaluate()

    assert(project.tasks.findByName("generateModulesGraphvizText") != null)
  }

  @Test
  fun testPrintTaskStatisticsIsAdded() {
    project.plugins.apply(ModuleGraphAssertionsPlugin::class.java)
    project.evaluate()

    assert(project.tasks.findByName("generateModulesGraphStatistics") != null)
  }
}
