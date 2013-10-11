include 'src/main/resources/scripts/cutOp.groovy'
include 'src/main/resources/scripts/rotateOp.groovy'
include 'src/main/resources/scripts/inflateOp.groovy'
include 'src/main/resources/scripts/resampleOp.groovy'
include 'src/main/resources/scripts/lineRenderer.groovy'
include 'src/main/resources/scripts/pointRenderer.groovy'
include 'src/main/resources/scripts/displacementRenderer.groovy'

def stroke( geom, strokeSize, angle ) {
  rotate( cut( geom, strokeSize, 0 ), angle )
}

def initialize() {
}

def render() {
  scale = viewport.getScale()
  strokeSize = 5 * scale
  nullFunction = new ConstantFunction( 0 );
  
  width = new ConstantFunction( 1 * viewport.getScale() );
  shift = new SinFunction( 20, 10 * viewport.getScale(), 0, 0 );
  sinInflater = new InflateOperator( width, shift, 3 );
  constantInflater = new InflateOperator( width, nullFunction, 20 );
  
  brushShift = new InverseSigmoidFunction( 1, 0.5, 5)
  brushWidth = new GaussFunction( 0.7 * scale,0.5,0.15 )
  brushInflater = new InflateOperator( brushWidth, brushShift, 2 );
  displacementColor = new Color( 1,1,0.1,0.9)
  roadBgColor = new Color( 0.25,0.25, 0.25, 0.7)
  
  double expandFactor = 2
  glPolygonMode( GL_FRONT_AND_BACK, GL_FILL );
  
  for ( DrawingPrimitive primitive : primitives ) {
  //  transformedPrimitives = split(resample(primitive, strokeSize, true ))
  //  transformedPrimitives = reparameterizeUnit( homothety( rotate(split(resample(primitive, strokeSize, false )), PI/12), expandFactor ))
  //  lineRendering( primitive, 1, Color.black )
  //  pointRendering( primitive, 5, Color.red, 5, Color.pink )
  
    displacementRendering( primitive, constantInflater, roadBgColor )
  //  displacementRendering( transformedPrimitives, brushInflater, displacementColor )
  
    //  lineRendering( inflate(primitive, 10 ), 1, Color.black )
  //  lineRendering( stroke(primitive, strokeSize, PI/4 ), 1, Color.black )
  //  pointRendering( stroke(primitive, strokeSize, PI/4 ), 3, Color.red, 1, Color.black )
  }
  
  for ( DrawingPrimitive primitive : primitives ) {
  //  transformedPrimitives = split(resample(primitive, strokeSize, true ))
    transformedPrimitives = reparameterizeUnit( homothety( rotate(split(resample(primitive, strokeSize, false )), PI/12), expandFactor ))
  //  lineRendering( primitive, 1, Color.black )
  //  pointRendering( primitive, 5, Color.red, 5, Color.pink )
  
  //  displacementRendering( primitive, constantInflater, roadBgColor )
    displacementRendering( transformedPrimitives, brushInflater, displacementColor )
  
  //    lineRendering( inflate(primitive, 10 ), 1, Color.black )
  //  lineRendering( stroke(primitive, strokeSize, PI/4 ), 1, Color.black )
  //  pointRendering( stroke(primitive, strokeSize, PI/4 ), 3, Color.red, 1, Color.black )
  }
  
  //  width = new ConstantFunction( 10 * viewport.getScale() );
  //  shift = new SinFunction( 20, 10 * viewport.getScale(), 0, 0 );
}