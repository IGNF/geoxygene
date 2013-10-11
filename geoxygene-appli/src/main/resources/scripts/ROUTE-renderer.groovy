include 'src/main/resources/scripts/cutOp.groovy'
include 'src/main/resources/scripts/rotateOp.groovy'
include 'src/main/resources/scripts/inflateOp.groovy'
include 'src/main/resources/scripts/resampleOp.groovy'
include 'src/main/resources/scripts/lineRenderer.groovy'
include 'src/main/resources/scripts/pointRenderer.groovy'
include 'src/main/resources/scripts/inspect.groovy'
include 'src/main/resources/scripts/displacementRenderer.groovy'

def initialize() {
  renderer = new FillRenderer( Color.blue )
  global.put("glRenderer", renderer )

}

def inflate( primitive ) {
    scale = viewport.getScale()

    fSin = new SinFunction(0.2 * scale ,2 * scale )
    fCte = new ConstantFunction( 4 * scale )
    fZero = new ConstantFunction( 0 )
    fLin = new LinearFunction( 0.5 * scale, 5  )
    width = new ComposeFunction(fCte, fCte )
    shift = new ConstantFunction( 0 );
    inflator = new InflateOperator( width, shift , 1 );

    inflator.setViewport( viewport )
    inflator.setInput( primitive );
    inflator.apply();
}

def renderOutlined( primitive, outlineWidth ) {
    outlineOp = new OutlineOperator( outlineWidth, CapStyle.ROUND, JoinStyle.ROUND )
    outlineOp.setViewport( viewport )
    outlineOp.setInput( primitive )
    outlineOp.apply();
    
    renderer = new GLPrimitiveRenderer( Color.black, Color.black )
    renderer.setViewport( viewport )
    renderer.setPrimitive( primitive )
    renderer.render()
}

def render() {


  glRenderer = global.get("glRenderer")
  scale = viewport.getScale()
  glRenderer.setViewport( viewport )
  glRenderer.setFillColor( Color.black )
   for ( DrawingPrimitive primitive : primitives ) {
    outlineOp = new OutlineOperator( 6f * scale, CapStyle.BUTT, JoinStyle.ROUND )
    outlineOp.setViewport( viewport )
    outlineOp.setInput( primitive )
    primitive = outlineOp.apply();

//    inspectPrimitive( primitive )
    glRenderer.setPrimitive( primitive )
    glRenderer.render()
//    renderOutlined( primitive, 2f * scale )
  }
  
  glRenderer.setFillColor( Color.white )
  for ( DrawingPrimitive primitive : primitives ) {
    outlineOp = new OutlineOperator( 4f * scale, CapStyle.BUTT, JoinStyle.ROUND )
    outlineOp.setViewport( viewport )
    outlineOp.setInput( primitive )
    primitive = outlineOp.apply();

//    inspectPrimitive( primitive )
    glRenderer.setPrimitive( primitive )
    glRenderer.render()
//    renderOutlined( primitive, 2f * scale )
  }
  

/*  paramRenderer = new ParameterPrimitiveRenderer( )
  paramRenderer.setViewport(viewport)
  paramRenderer.setNbParametersToSkip( 100 )
  for ( DrawingPrimitive primitive : primitives ) {
    paramRenderer.setPrimitive( inflate( primitive ) )
    paramRenderer.render()
  } 
*/
}

