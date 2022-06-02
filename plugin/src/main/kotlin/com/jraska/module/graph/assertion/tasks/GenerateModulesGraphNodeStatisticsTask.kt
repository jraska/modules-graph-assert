package com.jraska.module.graph.assertion.tasks

import com.jakewharton.picnic.renderText
import com.jakewharton.picnic.table
import com.jraska.module.graph.DependencyGraph
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.jgrapht.alg.scoring.BetweennessCentrality
import org.jgrapht.graph.DefaultDirectedGraph
import org.jgrapht.graph.DefaultEdge

open class GenerateModulesGraphNodeStatisticsTask : DefaultTask() {
  @Input
  lateinit var configurationsToLook: Set<String>

  @TaskAction
  fun run() {
    val dependencyGraph = project.createDependencyGraph(configurationsToLook)
    val nodeStatistics = dependencyGraph.nodeStatistics()
    table {
      cellStyle {
        paddingLeft = 1
        paddingRight = 1
      }
      header {
        row(
          "node",
          "betweennessCentrality",
          "degree",
          "inDegree",
          "outDegree",
          "height"
        )
      }
      nodeStatistics.forEach {
        row(
          it.node,
          "%.2f".format(it.betweennessCentrality),
          it.degree,
          it.inDegree,
          it.outDegree,
          it.height
        )
      }
    }.renderText().let(::println)
  }

  data class NodeStatistics(
    val node: String,
    val betweennessCentrality: Double,
    val degree: Int,
    val inDegree: Int,
    val outDegree: Int,
    val height: Int
  )

  private fun DependencyGraph.nodeStatistics(): List<NodeStatistics> {
    val g = toJGraphTGraph()
    val betweennessCentrality = BetweennessCentrality(g).scores
    return g.vertexSet().map { node ->
      NodeStatistics(
        node = node,
        betweennessCentrality = requireNotNull(betweennessCentrality[node]) {
          "Betweenness not found for $node"
        },
        degree = g.degreeOf(node),
        inDegree = g.inDegreeOf(node),
        outDegree = g.outDegreeOf(node),
        height = heightOf(node)
      )
    }.sortedByDescending {
      it.betweennessCentrality
    }
  }

  private fun DependencyGraph.toJGraphTGraph(): DefaultDirectedGraph<String, DefaultEdge> {
    val g = DefaultDirectedGraph<String, DefaultEdge>(DefaultEdge::class.java)
    nodes().forEach {
      g.addVertex(it.key)
    }
    nodes().forEach { n1 ->
      n1.dependsOn.forEach { n2 ->
        g.addEdge(n1.key, n2.key)
      }
    }
    return g
  }
}
