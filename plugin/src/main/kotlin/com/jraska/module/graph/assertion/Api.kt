package com.jraska.module.graph.assertion

object Api {
  object Tasks {
    const val GENERATE_GRAPHVIZ = "generateModulesGrapvizText"

    const val ASSERT_ALL = "assertModulesGraph"
    const val ASSERT_MAX_HEIGHT = "assertMaxHeight"
    const val ASSERT_LAYER_ORDER = "assertModuleLayers"
    const val ASSERT_USER_RULES = "assertRestrictions"
  }

  object Parameters {
    const val PRINT_STATISTICS = "modules.graph.print.statistics"
    const val PRINT_ONLY_MODULE = "modules.graph.of.module"
    const val OUTPUT_PATH = "modules.graph.output.gv"
  }
  
  const val EXTENSION_ROOT = "moduleGraphAssert"
}

