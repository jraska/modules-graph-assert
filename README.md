# Module Graph Assert
Gradle plugin to keep your modules graph healthy and lean.

[![CircleCI](https://circleci.com/gh/jraska/modules-graph-assert.svg?style=svg)](https://circleci.com/gh/jraska/modules-graph-assert)

## Assert your modules graph
<img width="1281" alt="example_graph" src="https://user-images.githubusercontent.com/6277721/70832705-18980e00-1df6-11ea-8b78-fc07ba570a2b.png">

## Usage
```groovy
buildscript {
  repositories {
    maven { url "https://plugins.gradle.org/m2/" }
  }
  dependencies {
    classpath 'gradle.plugin.project:plugin:0.4.1'
  }
}

apply plugin: 'com.jraska.module.graph.assertion'
```

#### Configuration
```groovy
moduleGraphAssert {
  maxHeight = 4
  moduleLayers = [":feature:\\S*", ":lib\\S*", ":core\\S*"] // modules prefixed with ":feature:" -> prefix ":lib:" -> prefix :core:
  moduleLayersExclude = [":feature-about -> :lib-legacy-about"]
  restricted = [':feature-[a-z]* -X> :forbidden-to-depend-on'] //regex to match module names
}
```

### Helper Graphviz Graph Export
- Visualising graph could be useful to see the dependency problem, therefore helper `generateModulesGraphvizText` is included.
- By default it generates graph of all modules in a project.
- Longest path of the project is in red.
- Adding parameter `modules.graph.print.statistics` prints also information about the graph.
- You can set `modules.graph.of.module` parameter if you want only subgraph of total graph.
```
./gradlew generateModulesGraphvizText -Pmodules.graph.print.statistics=true -Pmodules.graph.of.module=:feature-one
```
- Adding parameter `modules.graph.output.gv` saves the graphViz file to specified path
```
./gradlew generateModulesGraphvizText -Pmodules.graph.output.gv=all_modules
```
