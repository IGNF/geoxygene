// inflate a primitive, then render it using GL_QUAD_STRIP
 
def displacementRendering( primitive, inflater, color, viewport ) {
    
  renderer = new DisplacementPrimitiveRenderer( inflater )
  renderer.setPrimitive( primitive )
  renderer.setViewport( viewport )
  float r = color.getRed() / 255
  float g = color.getGreen() / 255
  float b = color.getBlue() / 255
  float a = color.getAlpha() / 255
  glColor4f( r,g,b,a)
  renderer.render()
}