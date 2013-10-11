// default rendering drawing points
include 'src/main/resources/scripts/lineRenderer.groovy'


def initialize() {
   renderer = new GLPrimitiveRenderer()
    renderer.setBackgroundColor( Color.black )
    renderer.setForegroundColor( Color.black )
    global.put( "glDefaultRenderer", renderer)
}

def render() {
    renderer = global.get("glDefaultRenderer")
    renderer.setViewport( viewport )
    renderer.setPrimitives( primitives )
    renderer.render();
}
