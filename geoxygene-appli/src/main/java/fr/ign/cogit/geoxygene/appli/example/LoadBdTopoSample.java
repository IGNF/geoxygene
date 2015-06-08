package fr.ign.cogit.geoxygene.appli.example;

import java.awt.event.ActionEvent;
import java.awt.geom.NoninvertibleTransformException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JMenu;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.appli.GeOxygeneApplication;
import fr.ign.cogit.geoxygene.appli.api.ProjectFrame;
import fr.ign.cogit.geoxygene.appli.plugin.AbstractGeOxygeneApplicationPlugin;
import fr.ign.cogit.geoxygene.util.conversion.ShapefileReader;

/**
 * 
 * @author MDVan-Damme
 */
public class LoadBdTopoSample extends AbstractGeOxygeneApplicationPlugin {
  
  /** Logger. */
  static final Logger LOGGER = Logger.getLogger(SLDDemoApplication.class.getName());
  
  @Override
  public void initialize(final GeOxygeneApplication application) {
    this.application = application;
    JMenu menuAwt = addMenu("Example", "Load BDUni Sample");
    application.getMainFrame().getMenuBar()
      .add(menuAwt, application.getMainFrame().getMenuBar().getMenuCount() - 2);
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    displayBdTopo();
  }
  
  
  
  public void displayBdTopo() {
    
    URL vegetationURL;
    URL surfaceEauURL;
    URL batiIndifferencieURL;
    URL routeURL;
    URL toponymeURL;
    
    ProjectFrame projectFrame = application.getMainFrame().newProjectFrame();
    
    try {
      vegetationURL = new URL("file", "", "./data/72_BDTOPO_2-0_LAMB93_SHP_X062-ED111/Extrait1/ZONE_VEGETATION.SHP");
      IPopulation<IFeature> vegetationPop = ShapefileReader.read(vegetationURL.getPath());
      projectFrame.addUserLayer(vegetationPop, "Végétation", null);
      
      surfaceEauURL = new URL("file", "", "./data/72_BDTOPO_2-0_LAMB93_SHP_X062-ED111/Extrait1/SURFACE_EAU.SHP");
      IPopulation<IFeature> surfaceEauPop = ShapefileReader.read(surfaceEauURL.getPath());
      projectFrame.addUserLayer(surfaceEauPop, "Surface d'eau", null);
      
      batiIndifferencieURL = new URL("file", "", "./data/72_BDTOPO_2-0_LAMB93_SHP_X062-ED111/Extrait1/BATI_INDIFFERENCIE.SHP");
      IPopulation<IFeature> batiIndifferenciePop = ShapefileReader.read(batiIndifferencieURL.getPath());
      projectFrame.addUserLayer(batiIndifferenciePop, "Bati indifférencié", null);
      
      routeURL = new URL("file", "", "./data/72_BDTOPO_2-0_LAMB93_SHP_X062-ED111/Extrait1/ROUTE.SHP");
      IPopulation<IFeature> routePop = ShapefileReader.read(routeURL.getPath());
      projectFrame.addUserLayer(routePop, "Routes", null);
      
      toponymeURL = new URL("file", "", "./data/72_BDTOPO_2-0_LAMB93_SHP_X062-ED111/Extrait1/TOPONYME_DIVERS.SHP");
      IPopulation<IFeature> toponymePop = ShapefileReader.read(toponymeURL.getPath());
      projectFrame.addUserLayer(toponymePop, "Toponymes", null);
    
    } catch (MalformedURLException e1) {
      e1.printStackTrace();
    }
    
    // SLD
    
    try {
      projectFrame.getLayerViewPanel().getViewport().zoomToFullExtent();
    } catch (NoninvertibleTransformException e) {
      e.printStackTrace();
    }
  }
  

}
