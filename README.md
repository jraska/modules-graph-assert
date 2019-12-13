# Module Graph Assert
Gradle plugin to keep your modules graph healthy and lean.

[![CircleCI](https://circleci.com/gh/jraska/modules-graph-assert.svg?style=svg)](https://circleci.com/gh/jraska/modules-graph-assert)

## Assert your modules graph
<img width="1281" alt="example_graph" src="https://user-images.githubusercontent.com/6277721/70832705-18980e00-1df6-11ea-8b78-fc07ba570a2b.png">

## Configuration
```groovy
moduleGraphAssert {
  maxHeight = 4
  moduleLayersFromTheTop = [":feature", ":lib", ":core"]
  restrinctInLayerDependencies = [":feature", ":lib"]
}
```
