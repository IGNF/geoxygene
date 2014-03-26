//include 'src/main/resources/scripts/cutOp.groovy'

renderer = new GLPrimitiveRenderer()
renderer.setTextureFilename( "./src/main/resources/textures/dense pine forest.jpg" )
renderer.setViewport( viewport )
renderer.setPrimitives( primitives )
renderer.render();
