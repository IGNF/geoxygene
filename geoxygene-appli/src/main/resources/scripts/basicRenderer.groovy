import fr.ign.cogit.geoxygene.appli.render.AbstractPrimitiveRenderer;
import fr.ign.cogit.geoxygene.appli.render.GLPrimitiveRenderer;
import fr.ign.cogit.geoxygene.appli.render.RenderingException;

  
class MyRenderer extends AbstractPrimitiveRenderer {

  GLPrimitiveRenderer gl = new GLPrimitiveRenderer();
  
  void setViewport( Viewport viewport ) {
    super.setViewport( viewport );
    gl.setViewport( viewport );
  }
  
  void render() throws RenderingException {
    
    gl.removeAllPrimitives();
    gl.addPrimitives( this.getPrimitives() );
    gl.render();
  }

}

renderer = new MyRenderer();