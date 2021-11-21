package com.jraska.module.graph.assertion

import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class FullProjectGradleTest {
  @get:Rule
  val testProjectDir: TemporaryFolder = TemporaryFolder()

  @Before
  fun setup() {
    testProjectDir.newFile("settings.gradle").writeText("include ':app', ':core', ':feature', 'core-api'")

    createModule(
      "core-api", content = """
      apply plugin: 'java-library'
      
      ext.moduleNameAssertAlias = "Api"
      """
    )

    createModule(
      "core", content = """
      apply plugin: 'java-library'

      dependencies {
        implementation project(":core-api")
      }
      ext.moduleNameAssertAlias = "Implementation"
      """
    )

    createModule(
      "feature", content = """
      apply plugin: 'java-library'

      dependencies {
        implementation project(":core-api")
      }
      
      ext.moduleNameAssertAlias = "Implementation"
      """
    )

    createModule(
      "app", content = """
          plugins {
              id 'com.jraska.module.graph.assertion'
          }
          apply plugin: 'java-library'
          
          moduleGraphAssert {
            maxHeight = 2
            allowed = ['Implementation -> Api', 'App -> .*']
            restricted = ['Api -X> Api', 'Implementation -X> Implementation']
          }
          
          dependencies {
            implementation project(":core-api")
            implementation project(":core")
            implementation project(":feature")
          }
     
          ext.moduleNameAssertAlias = "App"
      """
    )
  }

  @Test
  fun supportsConfigurationCache() {
    runGradleAssertModuleGraph(testProjectDir.root)
    val secondRunResult = runGradleAssertModuleGraph(testProjectDir.root)

    assert(secondRunResult.output.contains("Reusing configuration cache."))
  }

  @Test
  fun printsCorrectStatistics() {
    val output = runGradleAssertModuleGraph(testProjectDir.root, "generateModulesGraphStatistics").output

    assert(output.contains("GraphStatistics(modulesCount=4, edgesCount=5, height=2, longestPath=':app -> :core -> :core-api')"))
  }

  private fun createModule(dir: String, content: String) {
    val newFolder = testProjectDir.newFolder(dir)
    File(newFolder, "build.gradle").writeText(content)
  }
}
