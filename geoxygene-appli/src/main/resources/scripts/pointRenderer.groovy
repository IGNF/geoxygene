// take all points of a primitive and draw lines between them


def pointRendering( primitive, exThickness, exColor, inThickness, inColor ) {
    if (!primitive.isLeaf()) {
      for (DrawingPrimitive child : primitive.getPrimitives()) {
        pointRendering(child, exThickness, exColor, inThickness, inColor);
      }
      return;
    } else {
  glPointSize( (float)exThickness ); 
  glColor3f( exColor.getRed()/255, exColor.getGreen()/255, exColor.getBlue()/255 );
  glBegin(GL_POINTS);
  Point2d e1 = primitive.getPoint(0);
  glVertex2d(e1.x, e1.y);
  Point2d e2 = primitive.getPoint(primitive.getPointCount()-1);
  glVertex2d(e2.x, e2.y);
  glEnd();


  glPointSize( (float)inThickness ); 
  glColor3f( inColor.getRed()/255, inColor.getGreen()/255, inColor.getBlue()/255 );
  glBegin(GL_POINTS);
  for ( int n = 1; n < primitive.getPointCount()-1; n++) {
    Point2d p2 = primitive.getPoint(n);
    glVertex2d(p2.x, p2.y);
  }
  glEnd();
    }
}


def pointRendering( primitive, thickness, color ) {
    if (!primitive.isLeaf()) {
      for (DrawingPrimitive child : primitive.getPrimitives()) {
        pointRendering(child, thickness, color);
      }
      return;
    } else {
  glPointSize( (float)thickness );
  glColor3f( color.getRed()/255, color.getGreen()/255, color.getBlue()/255 );
  glBegin(GL_POINTS);
  for ( int n = 0; n < primitive.getPointCount(); n++) {
    Point2d p2 = primitive.getPoint(n);
    glVertex2d(p2.x, p2.y);
  }
  glEnd();
}
}

def pointRendering( primitive ) {
  pointRendering( primitive, 2 , Color.red , 2 , Color.black);
}

