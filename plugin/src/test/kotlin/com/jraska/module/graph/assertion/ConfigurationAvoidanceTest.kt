package com.jraska.module.graph.assertion

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class ConfigurationAvoidanceTest {
  @get:Rule
  val testProjectDir: TemporaryFolder = TemporaryFolder()

  @Before
  fun setup() {
    testProjectDir.newFile("build.gradle").writeText(
      """
          plugins {
              id 'com.jraska.module.graph.assertion'
          }
          
          moduleGraphAssert {
            maxHeight = 3
            allowed = [':app -> .*', ':feature-\\S* -> :lib\\S*', '.* -> :core', ':feature-one -> :feature-exclusion-test', ':feature-one -> :feature-one:nested']
            restricted = [':feature-[a-z]* -X> :feature-[a-z]*']
          }

          ext.moduleNameAssertAlias = "Alias"
      """,
    )
  }

  @Test
  fun tasksAreUpToDate() {
    runGradleAssertModuleGraph(testProjectDir.root)
    val secondRunResult = runGradleAssertModuleGraph(testProjectDir.root)

    val findAll = Regex("UP-TO-DATE").findAll(secondRunResult.output)
    assert(findAll.count() == 4)
  }
}

fun runGradleAssertModuleGraph(
  dir: File,
  vararg arguments: String = arrayOf("--configuration-cache", "assertModuleGraph"),
): BuildResult {
  return GradleRunner.create()
    .withProjectDir(dir)
    .withPluginClasspath()
    .withArguments(arguments.asList())
    .build()
}
