def initialize() {
    renderer = new GLPrimitiveRenderer()
    renderer.setTextureFilename( "./src/main/resources/textures/tarmac.jpg" )
    global.put("tarmacRenderer", renderer );
 
    strokeRenderer = new OutlinePrimitiveRenderer()
    strokeRenderer.setColor( Color.yellow )

    global.put("strokeRenderer", strokeRenderer );
}

def render() {
    scale = viewport.getScale()
    renderer = global.get("tarmacRenderer")
    renderer.setViewport( viewport )
    renderer.setPrimitives( primitives )
    renderer.render();
    
    strokeRenderer = global.get("strokeRenderer")
    strokeRenderer.setPrimitives( primitives )
    strokeRenderer.setLineWidth( 5 * scale )
    strokeRenderer.setViewport( viewport )
    strokeRenderer.render();

}
