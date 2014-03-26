// default rendering drawing points
include 'src/main/resources/scripts/pointRenderer.groovy'
include 'src/main/resources/scripts/lineRenderer.groovy'


def initialize() {
  // nothing to initialize
}

def render() {
    for ( DrawingPrimitive primitive : primitives )
    pointRendering( primitive )
}
