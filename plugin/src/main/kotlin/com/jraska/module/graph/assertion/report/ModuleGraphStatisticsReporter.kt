package com.jraska.module.graph.assertion.report

import com.jraska.module.graph.GraphStatistics
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import org.gradle.api.invocation.Gradle
import java.io.File

object ModuleGraphStatisticsReporter {
  private const val REPORT_FILE = "reports/graph/statistics/report.json"

  fun report(gradle: Gradle, statistics: List<GraphStatistics>) {
    File(gradle.rootProject.buildDir, REPORT_FILE)
      .createDirsAndFile()
      .writeText(Json.encodeToString(ListSerializer(GraphStatistics.serializer()), statistics))
  }

  private fun File.createDirsAndFile() = run {
    mkdirs()
    delete()
    createNewFile()
    this
  }
}
