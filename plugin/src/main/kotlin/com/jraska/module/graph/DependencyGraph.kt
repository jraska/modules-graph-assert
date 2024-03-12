package com.jraska.module.graph

import java.io.Serializable

class DependencyGraph private constructor() {
  private val nodes = mutableMapOf<String, Node>()

  fun findRoot(): Node {
    require(nodes.isNotEmpty()) { "Dependency Tree is empty" }

    val rootCandidates = nodes().toMutableSet()

    nodes().flatMap { it.dependsOn }
      .forEach { rootCandidates.remove(it) }

    return rootCandidates.associateBy { heightOf(it.key) }
      .maxByOrNull { it.key }!!.value
  }

  fun nodes(): Collection<Node> = nodes.values

  fun dependencyPairs(): List<Pair<String, String>> {
    return nodes()
      .flatMap { parent ->
        parent.dependsOn.map { dependency -> parent to dependency }
      }
      .map { it.first.key to it.second.key }
  }

  fun longestPath(): LongestPath {
    return longestPath(findRoot().key)
  }

  fun longestPath(key: String): LongestPath {
    val nodeNames =
      nodes.getValue(key)
        .longestPath()
        .map { it.key }

    return LongestPath(nodeNames)
  }

  fun height(): Int {
    return heightOf(findRoot().key)
  }

  fun heightOf(key: String): Int {
    return nodes.getValue(key).height()
  }

  fun statistics(): GraphStatistics {
    val height = height()
    val edgesCount = countEdges()
    return GraphStatistics(
      modulesCount = nodes.size,
      edgesCount = edgesCount,
      height = height,
      longestPath = longestPath(),
    )
  }

  fun subTree(key: String): DependencyGraph {
    require(nodes.contains(key)) { "Dependency Tree doesn't contain module: $key" }

    val connections = mutableListOf<Pair<String, String>>()
    addConnections(nodes.getValue(key), connections)

    return if (connections.isEmpty()) {
      createSingular(key)
    } else {
      create(connections)
    }
  }

  fun serializableGraph(): SerializableGraph {
    return SerializableGraph(
      ArrayList(dependencyPairs()),
      nodes.keys.first(),
    )
  }

  private fun addConnections(
    node: Node,
    into: MutableList<Pair<String, String>>,
  ) {
    node.dependsOn.forEach {
      into.add(node.key to it.key)
      addConnections(it, into)
    }
  }

  private fun countEdges(): Int {
    return nodes().flatMap { node -> node.dependsOn }.count()
  }

  class SerializableGraph(
    val dependencyPairs: ArrayList<Pair<String, String>>,
    val firstModule: String,
  ) : Serializable

  class Node(val key: String) {
    val dependsOn = mutableSetOf<Node>()

    private val calculatedHeight by lazy {
      if (isLeaf()) {
        0
      } else {
        1 + dependsOn.maxOf { it.height() }
      }
    }

    private fun isLeaf() = dependsOn.isEmpty()

    fun height(): Int {
      return calculatedHeight
    }

    internal fun longestPath(): List<Node> {
      if (isLeaf()) {
        return listOf(this)
      } else {
        val path = mutableListOf(this)

        val maxHeightNode = dependsOn.maxByOrNull { it.height() }!!
        path.addAll(maxHeightNode.longestPath())

        return path
      }
    }
  }

  companion object {
    fun createSingular(singleModule: String): DependencyGraph {
      val dependencyGraph = DependencyGraph()

      dependencyGraph.getOrCreate(singleModule)

      return dependencyGraph
    }

    fun create(dependencies: List<Pair<String, String>>): DependencyGraph {
      require(dependencies.isNotEmpty()) { "Graph cannot be empty. Use createSingular for cases with no dependencies" }

      val graph = DependencyGraph()
      dependencies.forEach { graph.addEdge(it.first, it.second) }
      return graph
    }

    fun create(vararg dependencies: Pair<String, String>): DependencyGraph {
      return create(dependencies.asList())
    }

    fun create(graph: SerializableGraph): DependencyGraph {
      if (graph.dependencyPairs.isEmpty()) {
        return createSingular(graph.firstModule)
      } else {
        return create(graph.dependencyPairs)
      }
    }

    private fun DependencyGraph.addEdge(
      from: String,
      to: String,
    ) {
      getOrCreate(from).dependsOn.add(getOrCreate(to))
    }

    private fun DependencyGraph.getOrCreate(key: String): Node {
      return nodes[key] ?: Node(key).also { nodes[key] = it }
    }
  }
}
