include 'src/main/resources/scripts/cutOp.groovy'
include 'src/main/resources/scripts/rotateOp.groovy'
include 'src/main/resources/scripts/inflateOp.groovy'
include 'src/main/resources/scripts/resampleOp.groovy'
include 'src/main/resources/scripts/lineRenderer.groovy'
include 'src/main/resources/scripts/pointRenderer.groovy'
include 'src/main/resources/scripts/displacementRenderer.groovy'

def initialize() {
    renderer = new GLPrimitiveRenderer()
    renderer.setBackgroundColor( Color.blue )
    renderer.setForegroundColor( Color.blue )
    global.put("glRenderer", renderer );
    renderer2 = new OutlinePrimitiveRenderer()
    renderer2.setColor( Color.black )
    renderer2.setLineWidth( 2 )
    global.put("outlineRenderer", renderer2 );
}

def render() {
  renderer = global.get("glRenderer")
  outlineRenderer = global.get("outlineRenderer")
  
  scale = viewport.getScale()
  width = new PiecewiseFunction( );
  a = 10 * scale;
  width.addPiece(-1000, 0.50005, new LinearFunction(2*a,0))
  width.addPiece(0.4999995, 1000, new LinearFunction(-2*a,2*a))
  shift = new LinearFunction( 2*a, -a );
  stripInflater = new InflateOperator( width, shift, 3 );

  strokeSize = a * 15
  nullFunction = new ConstantFunction( 0 );
  
  displacementColor = new Color( 1,1,0,1)
  roadBgColor = new Color( 0.25,0.25, 0.25, 0.7)
  
  renderer.setViewport( viewport )
  outlineRenderer.setViewport( viewport )
  double expandFactor = a
  renderer.setLineWidth( (float) (0.10f * a) )
  //glPolygonMode( GL_FRONT_AND_BACK, GL_LINE )
  for ( DrawingPrimitive primitive : primitives ) {

    transformedPrimitive = reparameterizeUnit( split( resample(primitive, strokeSize, false ) ) )
    
    stripInflater.addInput( transformedPrimitive )
    transformedPrimitive = stripInflater.apply()


  //  renderer.setPrimitive( primitive )
    renderer.setPrimitive( primitive )
    outlineRenderer.setPrimitive( transformedPrimitive )

        renderer.render();
    outlineRenderer.render()
    //displacementRendering( primitive, stripInflater, displacementColor, viewport )



  }
}
