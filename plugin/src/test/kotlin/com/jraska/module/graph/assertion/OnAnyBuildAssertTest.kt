package com.jraska.module.graph.assertion

import org.gradle.testkit.runner.GradleRunner
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class OnAnyBuildAssertTest {
  @get:Rule
  val testProjectDir: TemporaryFolder = TemporaryFolder()

  @Before
  fun setup() {
    testProjectDir.newFile("settings.gradle")
      .writeText("include ':app', ':core', ':feature', 'core-api'")

    createModule(
      "core-api",
      content = """
      apply plugin: 'java-library'
      
      ext.moduleNameAssertAlias = "Api"
      """,
    )

    createModule(
      "core",
      content = """
      apply plugin: 'java-library'

      dependencies {
        implementation project(":core-api")
      }
      ext.moduleNameAssertAlias = "Implementation"
      """,
    )

    createModule(
      "feature",
      content = """
      apply plugin: 'java-library'

      dependencies {
        implementation project(":core-api")
      }
      
      ext.moduleNameAssertAlias = "Implementation"
      """,
    )
  }

  private fun createAppModule(moduleGraphAssertConfiguration: String) {
    createModule(
      "app",
      content = """
          plugins {
              id 'com.jraska.module.graph.assertion'
          }
          apply plugin: 'java-library'
          
          $moduleGraphAssertConfiguration
          
          dependencies {
            implementation project(":core-api")
            implementation project(":core")
            implementation project(":feature")
          }
     
          ext.moduleNameAssertAlias = "App"
      """,
    )
  }

  @Test
  fun failsOnEvaluationCheckMaxHeight() {
    createAppModule(
      """
          moduleGraphAssert {
            maxHeight = 1
            assertOnAnyBuild = true
          }   
      """,
    )

    val output = setupGradle(testProjectDir.root, "help").buildAndFail().output

    assert(
      output.contains(
        "Module :app is allowed to have maximum height of 1, but has 2, problematic dependencies: :app -> :core -> :core-api",
      ),
    )
  }

  @Test
  fun whenNotAssertOnEachEvaluation_succeedsOnEvaluationCheckMaxHeight() {
    createAppModule(
      """
          moduleGraphAssert {
            maxHeight = 1
            assertOnAnyBuild = false
          }   
      """,
    )

    val output = setupGradle(testProjectDir.root, "help").build().output
    assert(output.contains("BUILD SUCCESS"))

    setupGradle(testProjectDir.root, "assertModuleGraph").buildAndFail()
  }

  @Test
  fun failsOnEvaluationCheckAllowed() {
    createAppModule(
      """
          moduleGraphAssert {
            allowed = ['Implementation -> Api', 'App -> Api']
            assertOnAnyBuild = true
          }   
      """,
    )

    val output = setupGradle(testProjectDir.root, "help").buildAndFail().output

    assert(
      output.contains(
        """["App"(':app') -> "Implementation"(':core'), "App"(':app') -> "Implementation"(':feature')] not allowed by any of ['Implementation -> Api', 'App -> Api']""",
      ),
    )
  }

  @Test
  fun whenNotAssertOnEachEvaluationDefault_succeedsOnEvaluationCheckAllowed() {
    createAppModule(
      """
          moduleGraphAssert {
            allowed = ['Implementation -> Api', 'App -> Api']
          }   
      """,
    )

    val output = setupGradle(testProjectDir.root, "help").build().output
    assert(output.contains("BUILD SUCCESS"))

    setupGradle(testProjectDir.root, "assertModuleGraph").buildAndFail()
  }

  @Test
  fun failsOnEvaluationCheckRestricted() {
    createAppModule(
      """
          moduleGraphAssert {
            restricted = ['App -X> Api', 'Implementation -X> Implementation']
            assertOnAnyBuild = true
          }   
      """,
    )

    val output = setupGradle(testProjectDir.root, "help").buildAndFail().output

    assert(output.contains("""Dependency '"App"(':app') -> "Api"(':core-api') violates: 'App -X> Api'"""))
  }

  private fun createModule(
    dir: String,
    content: String,
  ) {
    val newFolder = testProjectDir.newFolder(dir)
    File(newFolder, "build.gradle").writeText(content)
  }

  private fun setupGradle(
    dir: File,
    vararg arguments: String,
  ): GradleRunner {
    return GradleRunner.create()
      .withProjectDir(dir)
      .withPluginClasspath()
      .withArguments(arguments.asList())
  }
}
