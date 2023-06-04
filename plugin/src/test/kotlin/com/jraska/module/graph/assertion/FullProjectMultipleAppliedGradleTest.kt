package com.jraska.module.graph.assertion

import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class FullProjectMultipleAppliedGradleTest {
  @get:Rule
  val testProjectDir: TemporaryFolder = TemporaryFolder()

  @Before
  fun setup() {
    testProjectDir.newFile("settings.gradle")
      .writeText("include ':app', ':core', 'core-api', 'no-dependencies'")

    createModule(
      "core-api", content = """
      apply plugin: 'java-library'
      """
    )

    createModule(
      "core", content = """
      apply plugin: 'java-library'

      dependencies {
        implementation project(":core-api")
      }
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
          }
          
          dependencies {
            implementation project(":core-api")
            implementation project(":core")
          }
      """
    )

    createModule(
      "no-dependencies", content = """
          plugins {
              id 'com.jraska.module.graph.assertion'
          }
          apply plugin: 'java-library'
          
          moduleGraphAssert {
            maxHeight = 0
          }
      """
    )
  }

  @Test
  fun printsBothCorrectStatistics() {
    val output =
      runGradleAssertModuleGraph(testProjectDir.root, "generateModulesGraphStatistics").output

    println(output)
    assert(output.contains("> Task :app:generateModulesGraphStatistics\n" +
      "GraphStatistics(modulesCount=3, edgesCount=3, height=2, longestPath=':app -> :core -> :core-api')\n" +
      "\n" +
      "> Task :no-dependencies:generateModulesGraphStatistics\n" +
      "GraphStatistics(modulesCount=1, edgesCount=0, height=0, longestPath=':no-dependencies')"))
  }

  private fun createModule(dir: String, content: String) {
    val newFolder = testProjectDir.newFolder(dir)
    File(newFolder, "build.gradle").writeText(content)
  }
}
