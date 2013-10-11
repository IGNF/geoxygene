def initialize() {
    
    renderer = new FillRenderer()
    renderer.setTextureFilename( "./src/main/resources/textures/dense pine forest.jpg" )
    
    global.put("pineRenderer", renderer );
}

def render() {
    scale = viewport.getScale()
    
    renderer = global.get("pineRenderer")
    
    
    renderer.setViewport( viewport )
    renderer.setPrimitives( primitives )
    renderer.render();
    
}
