package com.jraska.module.graph.assertion

import org.gradle.api.internal.project.DefaultProject
import org.gradle.api.plugins.JavaLibraryPlugin
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
    project.plugins.apply(ModuleGraphAssertionsPlugin::class.java)
    project.evaluate()

    assert(project.tasks.findByName(Api.Tasks.ASSERT_ALL) != null)
    assert(project.tasks.findByName(Api.Tasks.ASSERT_ALL)!!.dependsOn.isEmpty())
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

    assert(project.tasks.findByName(Api.Tasks.ASSERT_MAX_HEIGHT) != null)
    assert(project.tasks.findByName(Api.Tasks.ASSERT_LAYER_ORDER) != null)
    assert(project.tasks.findByName(Api.Tasks.ASSERT_USER_RULES) != null)
  }
}
