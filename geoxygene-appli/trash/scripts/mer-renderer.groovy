def initialize() {
    renderer = new GLPrimitiveRenderer()
    renderer.setBackgroundColor( new Color(0.2, 0.3, 0.7, 0.9) )
    renderer.setForegroundColor( Color.blue )
//    renderer.setTextureFilename( "./src/main/resources/textures/water1.jpg" )
    global.put("waterRenderer", renderer );
}

def render() {
    glLineWidth( 1f )

    renderer = global.get("waterRenderer")

    renderer.setViewport( viewport )
    renderer.setPrimitives( primitives )
    renderer.render();
}
