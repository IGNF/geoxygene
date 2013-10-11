// default rendering drawing points
include 'src/main/resources/scripts/pointRenderer.groovy'
include 'src/main/resources/scripts/lineRenderer.groovy'

def initialize() {
   renderer = new GLPrimitiveRenderer()
    renderer.setBackgroundColor( new Color(0.2,0.2,0.5) )
    global.put( "glDefaultRenderer", renderer)
}

def render() {
    renderer = global.get("glDefaultRenderer")
    renderer.setViewport( viewport )
    renderer.setPrimitives( primitives )
    renderer.render();
}
