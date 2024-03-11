package com.jraska.module.graph.assertion

import com.jraska.module.graph.writer.GraphWriter
import com.jraska.module.graph.writer.GraphvizWriter
import com.jraska.module.graph.writer.MermaidWriter
import kotlin.reflect.KClass

enum class OutputFormat(val writer: KClass<out GraphWriter>) {
  GRAPHVIZ(GraphvizWriter::class),
  MERMAID(MermaidWriter::class),
  ;

  val isGraphviz: Boolean
    get() = this == GRAPHVIZ

  val isMermaid: Boolean
    get() = this == MERMAID
}
