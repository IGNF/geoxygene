//include 'src/main/resources/scripts/cutOp.groovy'

def initialize() {
    renderer = new GLPrimitiveRenderer()
    renderer.setTextureFilename( "./src/main/resources/textures/dense pine forest.jpg" )
    global.put("pineRenderer", renderer );
}

def render() {
    renderer = global.get("pineRenderer")
    renderer.setViewport( viewport )
    renderer.setPrimitives( primitives )
    renderer.render();
}