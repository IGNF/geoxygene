def initialize() {
    renderer = new GLPrimitiveRenderer()
    renderer.setBackgroundColor( Color.red )
    renderer.setForegroundColor( Color.blue )
//    renderer.setTextureFilename( "./src/main/resources/textures/water1.jpg" )
    global.put("glRenderer", renderer );
}

def render() {
    glLineWidth( 1f )

    renderer = global.get("glRenderer")

    renderer.setViewport( viewport )
    renderer.setPrimitives( primitives )
    renderer.render();
}
