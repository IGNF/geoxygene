// take all points of a primitive and draw lines between them
 
def lineRendering( primitive, thickness, color ) {
  if (!primitive.isLeaf()) {
    for (DrawingPrimitive childPrimitive : primitive.getPrimitives()) {
      lineRendering(childPrimitive, thickness, color );
    }
  } else {
    glLineWidth( (float)thickness ); 
    glColor3f( color.getRed()/255, color.getGreen()/255, color.getBlue()/255 );
    glBegin(GL_LINE_STRIP);
  
      for ( int n = 0; n < primitive.getPointCount(); n++) {
        Point2d p2 = primitive.getPoint(n);
        glVertex2d(p2.x, p2.y);
      }
    glEnd();
  }
}

