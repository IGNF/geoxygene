// default rendering drawing points
include 'src/main/resources/scripts/pointRenderer.groovy'
include 'src/main/resources/scripts/lineRenderer.groovy'


def initialize() {
   renderer = new GLPrimitiveRenderer()
    renderer.setBackgroundColor( Color.red )
    renderer.setForegroundColor( Color.red )
    global.put( "glDefaultRenderer", renderer)
}

def render() {
    renderer = global.get("glDefaultRenderer")
    renderer.setViewport( viewport )
    renderer.setPrimitives( primitives )
    glColor4d( 0,0,1,1)
    renderer.render();
}
