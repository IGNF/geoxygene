def initialize() {
    renderer = new FillRenderer()
    renderer.setTextureFilename( "./src/main/resources/textures/water1.jpg" )
    global.put("waterRenderer", renderer );
}

def render() {
    glLineWidth( 1f )
    renderer = global.get("waterRenderer")
    renderer.setViewport( viewport )
    renderer.setPrimitives( primitives )
    renderer.render();
}
