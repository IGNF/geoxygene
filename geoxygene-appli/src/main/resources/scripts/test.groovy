
//     vvvvvvv BOF '/home/turbet/projects/geoxygene/dev/geoxygene/geoxygene-appli/src/main/resources/scripts/strokeRenderer.groovy' vvvvvvv

//     vvvvvvv BOF '/home/turbet/projects/geoxygene/dev/geoxygene/geoxygene-appli/src/main/resources/scripts/cutOp.groovy' vvvvvvv
def cut( primitive, strokeSize, strokeGap ) {
  CutterOperator cutter = new CutterOperator(strokeSize, strokeGap );
  cutter.addInput(primitive);
  cutter.apply();
}

def cut( primitive, strokeSize ) {
  CutterOperator cutter = new CutterOperator(strokeSize, 0 );
  cutter.addInput(primitive);
  cutter.apply();
}

//     ^^^^^^^ EOF '/home/turbet/projects/geoxygene/dev/geoxygene/geoxygene-appli/src/main/resources/scripts/cutOp.groovy' ^^^^^^^



//     vvvvvvv BOF '/home/turbet/projects/geoxygene/dev/geoxygene/geoxygene-appli/src/main/resources/scripts/rotateOp.groovy' vvvvvvv

def rotate( primitive, angle ) {
  RotateOperator rotator = new RotateOperator( angle );
  rotator.addInput( primitive );
  rotator.apply();
}
//     ^^^^^^^ EOF '/home/turbet/projects/geoxygene/dev/geoxygene/geoxygene-appli/src/main/resources/scripts/rotateOp.groovy' ^^^^^^^



//     vvvvvvv BOF '/home/turbet/projects/geoxygene/dev/geoxygene/geoxygene-appli/src/main/resources/scripts/inflateOp.groovy' vvvvvvv
def inflate( primitive ) {
  width = new SinFunction( 10, 10, 0, 0 );
  shift = new SinFunction( 2, 10, 0, 0 );
  inflator = new InflaterOperator( width, shift , 10 );
  inflator.addInput( primitive );
  inflator.apply();
}

def inflate( primitive, width, shift, samplingRate ) {
  inflator = new InflaterOperator( width, shift , samplingRate );
  inflator.addInput( primitive );
  inflator.apply();
}//     ^^^^^^^ EOF '/home/turbet/projects/geoxygene/dev/geoxygene/geoxygene-appli/src/main/resources/scripts/inflateOp.groovy' ^^^^^^^



//     vvvvvvv BOF '/home/turbet/projects/geoxygene/dev/geoxygene/geoxygene-appli/src/main/resources/scripts/lineRenderer.groovy' vvvvvvv
// take all points of a primitive and draw lines between them
 
def lineRendering( primitive, thickness, color ) {
  if (!primitive.isLeaf()) {
    for (DrawingPrimitive childPrimitive : primitive.getPrimitives()) {
      lineRendering(childPrimitive, thickness, color );
    }
  } else {
    glLineWidth( thickness ); 
    glColor3f( color.getRed()/255, color.getGreen()/255, color.getBlue()/255 );
    glBegin(GL_LINE_STRIP);
  
      for ( int n = 0; n < primitive.getPointCount(); n++) {
        Point2d p2 = primitive.getPoint(n);
        glVertex2d(p2.x, p2.y);
      }
    glEnd();
  }
}
//     ^^^^^^^ EOF '/home/turbet/projects/geoxygene/dev/geoxygene/geoxygene-appli/src/main/resources/scripts/lineRenderer.groovy' ^^^^^^^



//     vvvvvvv BOF '/home/turbet/projects/geoxygene/dev/geoxygene/geoxygene-appli/src/main/resources/scripts/pointRenderer.groovy' vvvvvvv
// take all points of a primitive and draw lines between them


def pointRendering( primitive, exThickness, exColor, inThickness, inColor ) {
  glPointSize( exThickness ); 
  glColor3f( exColor.getRed()/255, exColor.getGreen()/255, exColor.getBlue()/255 );
  glBegin(GL_POINTS);
  Point2d e1 = primitive.getPoint(0);
  glVertex2d(e1.x, e1.y);
  Point2d e2 = primitive.getPoint(primitive.getPointCount()-1);
  glVertex2d(e2.x, e2.y);
  glEnd();


  glPointSize( inThickness ); 
  glColor3f( inColor.getRed()/255, inColor.getGreen()/255, inColor.getBlue()/255 );
  glBegin(GL_POINTS);
  for ( int n = 1; n < primitive.getPointCount()-1; n++) {
    Point2d p2 = primitive.getPoint(n);
    glVertex2d(p2.x, p2.y);
  }
  glEnd();
}


def pointRendering( primitive, thickness, color ) {
  glPointSize( thickness ); 
  glColor3f( color.getRed()/255, color.getGreen()/255, color.getBlue()/255 );
  glBegin(GL_POINTS);
  for ( int n = 0; n < primitive.getPointCount(); n++) {
    Point2d p2 = primitive.getPoint(n);
    glVertex2d(p2.x, p2.y);
  }
  glEnd();
}

def pointRendering( primitive ) {
  lineRendering( primitive, 2 , Color.black );
}

//     ^^^^^^^ EOF '/home/turbet/projects/geoxygene/dev/geoxygene/geoxygene-appli/src/main/resources/scripts/pointRenderer.groovy' ^^^^^^^



//     vvvvvvv BOF '/home/turbet/projects/geoxygene/dev/geoxygene/geoxygene-appli/src/main/resources/scripts/displacementRenderer.groovy' vvvvvvv
// inflate a primitive, then render it using GL_QUAD_STRIP
 
def displacementRendering( primitive, inflater, color ) {
  renderer = new DisplacementPrimitiveRenderer( inflater )
  renderer.setPrimitive( primitive )
  renderer.setViewport( viewport )
  renderer.render()
}//     ^^^^^^^ EOF '/home/turbet/projects/geoxygene/dev/geoxygene/geoxygene-appli/src/main/resources/scripts/displacementRenderer.groovy' ^^^^^^^



def stroke( geom, strokeSize, angle ) {
  rotate( cut( geom, strokeSize, 0 ), angle )
}

scale = viewport.getScale()
strokeSize = 5 * scale
width = new ConstantFunction( 10 * viewport.getScale() );
shift = new SinFunction( 20, 10 * viewport.getScale(), 0, 0 );
inflater = new InflaterOperator( width, shift, 3 );

for ( DrawingPrimitive primitive : primitives ) {
//displacementRendering( primitive, inflater, Color.green )
//  lineRendering( inflate(primitive, 10 ), 1, Color.black )
  lineRendering( stroke(primitive, strokeSize, PI/4 ), 1, Color.black )
  pointRendering( stroke(primitive, strokeSize, PI/4 ), 3, Color.red, 1, Color.black )
}

//  width = new ConstantFunction( 10 * viewport.getScale() );
//  shift = new SinFunction( 20, 10 * viewport.getScale(), 0, 0 );
//     ^^^^^^^ EOF '/home/turbet/projects/geoxygene/dev/geoxygene/geoxygene-appli/src/main/resources/scripts/strokeRenderer.groovy' ^^^^^^^

5 * 2