def initialize() {
  global.put("outlineRenderer", new OutlinePrimitiveRenderer( Color.red, 5. ) )
}

def render() {
  scale = viewport.getScale()
  outlineRenderer renderer = global.get("outlineRenderer")
  
  outlineRenderer.setPrimitives( primitives )
  outlineRenderer.setViewport( viewport )
  outlineRenderer.render()
  
}