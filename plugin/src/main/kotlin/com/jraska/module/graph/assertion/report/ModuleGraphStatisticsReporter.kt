package com.jraska.module.graph.assertion.report

import com.jraska.module.graph.GraphStatistics
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToStream
import org.gradle.api.invocation.Gradle
import java.io.File

@OptIn(ExperimentalSerializationApi::class)
object ModuleGraphStatisticsReporter {
  private const val REPORT_FILE = "reports/graph/statistics/report.json"

  fun report(gradle: Gradle, statistics: List<GraphStatistics>) {
    File(gradle.rootProject.buildDir, REPORT_FILE)
      .createDirsAndFile()
      .outputStream()
      .use { Json.encodeToStream(ListSerializer(GraphStatistics.serializer()), statistics, it) }
  }

  private fun File.createDirsAndFile() = run {
    mkdirs()
    delete()
    createNewFile()
    this
  }
}
