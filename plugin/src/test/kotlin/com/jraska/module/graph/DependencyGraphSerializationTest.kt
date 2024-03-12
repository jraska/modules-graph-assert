package com.jraska.module.graph

import org.junit.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

class DependencyGraphSerializationTest {
  @Test
  fun singularGraphIsSerializable() {
    val originalGraph = DependencyGraph.createSingular(":app")
    val deserializedGraph = serializeAndDeserializeGraph(originalGraph)

    assert(deserializedGraph.statistics() == originalGraph.statistics())
  }

  @Test
  fun graphIsSerializable() {
    val originalGraph =
      DependencyGraph.create(
        "feature" to "lib",
        "lib" to "core",
        "app" to "feature",
        "feature" to "core",
        "app" to "core",
      )

    val deserializedGraph = serializeAndDeserializeGraph(originalGraph)

    assert(deserializedGraph.statistics() == originalGraph.statistics())
  }

  private fun serializeAndDeserializeGraph(originalGraph: DependencyGraph): DependencyGraph {
    val byteArray = ByteArrayOutputStream()
    ObjectOutputStream(byteArray).writeObject(originalGraph.serializableGraph())

    val deserialized = ObjectInputStream(ByteArrayInputStream(byteArray.toByteArray())).readObject()
    return DependencyGraph.create(deserialized as DependencyGraph.SerializableGraph)
  }
}
