package com.jraska.module.graph.assertion

object Api {
  object Tasks {
    const val GENERATE_GRAPHVIZ = "generateModulesGraphvizText"
    const val GENERATE_GRAPH_STATISTICS = "generateModulesGraphStatistics"

    const val ASSERT_ALL = "assertModuleGraph"
    const val ASSERT_MAX_HEIGHT = "assertMaxHeight"
    const val ASSERT_LAYER_ORDER = "assertModuleLayers"
    const val ASSERT_USER_RULES = "assertRestrictions"
  }

  object Parameters {
    @Deprecated("Will be removed in version 2.0")
    const val PRINT_STATISTICS = "modules.graph.print.statistics"

    const val PRINT_ONLY_MODULE = "modules.graph.of.module"
    const val OUTPUT_PATH = "modules.graph.output.gv"
  }

  const val EXTENSION_ROOT = "moduleGraphAssert"

  val API_IMPLEMENTATON_CONFIGURATIONS = setOf("api", "implementation")
}

