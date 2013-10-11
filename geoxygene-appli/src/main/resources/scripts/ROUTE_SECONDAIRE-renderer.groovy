include 'src/main/resources/scripts/resampleOp.groovy'


def initialize() {

  lineRenderer = new LinePrimitiveRenderer( 4.0 )
  lineRenderer.setViewport(viewport)
  global.put( "lineRenderer", lineRenderer )

  lineRenderer = new ParameterPrimitiveRenderer( )
  lineRenderer.setViewport(viewport)
  global.put( "paramRenderer", lineRenderer )
}

def render() {
   // for ( DrawingPrimitive primitive : primitives )
 //   pointRendering( primitive, 10, Color.gray )
  scale = viewport.getScale()
  glColor3d(0,0,0)
  
  lineRenderer = global.get("lineRenderer")
  lineRenderer.setViewport(viewport)
  lineRenderer.setLineWidth( 50 * scale )
  for ( DrawingPrimitive primitive : primitives ) {
    lineRenderer.setPrimitive( resample(primitive,20) )
    lineRenderer.render()
  }

/*
  paramRenderer = global.get("paramRenderer")
  paramRenderer.setViewport(viewport)
  for ( DrawingPrimitive primitive : primitives ) {
    paramRenderer.setPrimitive( resample( primitive, 20) )
    paramRenderer.render()
  } 
*/  
}
