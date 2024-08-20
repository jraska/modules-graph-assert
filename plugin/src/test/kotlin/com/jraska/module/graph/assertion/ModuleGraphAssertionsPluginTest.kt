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

    val plugin = ModuleGraphAssertionsPlugin()
    plugin.addModulesAssertions(project, GraphRulesExtension())

    assert(project.tasks.findByName(Api.Tasks.ASSERT_ALL) != null)
    assert(project.tasks.findByName(Api.Tasks.ASSERT_ALL)!!.dependsOn.isEmpty())
    assert(project.tasks.findByName(CHECK_TASK_NAME)!!.dependsOn.size == checkDependsOnSize + 1)
  }

  @Test
  fun testAddsOnlyOneTaskWhenApplied2() {
    val plugin = ModuleGraphAssertionsPlugin()

    val extension = GraphRulesExtension().apply {
      maxHeight = 3
      allowed = arrayOf(":feature-\\S* -> :lib\\S*", ".* -> :core")
      restricted = arrayOf(":feature-one -X> :feature-two")
    }

    plugin.addModulesAssertions(project, extension)

    assert(project.tasks.findByName(Api.Tasks.ASSERT_ALL) != null)
    assert(project.tasks.findByName(Api.Tasks.ASSERT_ALL)!!.dependsOn.size == 3)

    setOf(
      project.tasks.findByName(Api.Tasks.ASSERT_MAX_HEIGHT) as AssertGraphTask,
      project.tasks.findByName(Api.Tasks.ASSERT_ALLOWED) as AssertGraphTask,
      project.tasks.findByName(Api.Tasks.ASSERT_RESTRICTIONS) as AssertGraphTask
    )
  }

  @Test
  fun testAddsOnlyOneTaskWhenApplied3() {
    val plugin = ModuleGraphAssertionsPlugin()

    val extension = GraphRulesExtension().apply {
      maxHeight = 3
      allowed = arrayOf(":feature-one -> :lib")
    }

    plugin.addModulesAssertions(project, extension)

    assert(project.tasks.findByName(Api.Tasks.ASSERT_ALL) != null)
    assert(project.tasks.findByName(Api.Tasks.ASSERT_ALL)!!.dependsOn.size == 2)

    setOf(
      project.tasks.findByName(Api.Tasks.ASSERT_MAX_HEIGHT) as AssertGraphTask,
      project.tasks.findByName(Api.Tasks.ASSERT_ALLOWED) as AssertGraphTask,
    )
  }
}
