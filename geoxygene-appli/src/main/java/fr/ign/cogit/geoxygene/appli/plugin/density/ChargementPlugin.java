package fr.ign.cogit.geoxygene.appli.plugin.density;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.geom.NoninvertibleTransformException;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.appli.plugin.density.tools.DensityPlugin;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.Population;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;
import fr.ign.cogit.geoxygene.style.Layer;
import fr.ign.cogit.geoxygene.util.conversion.ShapefileReader;



public class ChargementPlugin extends DensityPlugin{

  @Override
  public void actionPerformed(ActionEvent arg0) {
    String path = "";
    int choice = JOptionPane.showConfirmDialog(null, "Chargement rapide", "information", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);
    if(choice==2){
      return;
    }else if(choice==0){
      path = "C:\\Users\\Simon\\Dropbox\\Ecole\\ProjetRech\\Data-Density\\data\\4.shp";
    } else{
      JFileChooser fc = new JFileChooser("C:\\Users\\Simon\\Dropbox\\Ecole\\ProjetRech\\Data-Density\\data");
      FileNameExtensionFilter filter = new FileNameExtensionFilter("SHAPE File", "shp");
      fc.setFileFilter(filter);
      int returnVal = fc.showOpenDialog(null);

      if(returnVal==JFileChooser.CANCEL_OPTION)
        return;
      else if(returnVal==JFileChooser.APPROVE_OPTION)
        path = fc.getSelectedFile().getAbsolutePath();
    }
    
    Population<DefaultFeature> pop = new Population<DefaultFeature>("Points");
    projectFrame.getDataSet().addPopulation(pop);
    
    Layer layer = projectFrame.getSld().createLayer("Points",GM_Point.class, Color.green, Color.red, 1f, 1);
    layer.getSymbolizer().setUnitOfMeasurePixel();
    
    projectFrame.getSld().add(layer);

    IFeatureCollection<IFeature> featCollInitiale = ShapefileReader.read(path);
    
    for (IFeature iFeature : featCollInitiale) {
      DirectPosition dp = new DirectPosition(iFeature.getGeom().centroid().getX(), iFeature.getGeom().centroid().getY());
      GM_Point p = new GM_Point(dp);
      pop.add(new DefaultFeature(p));
    }
    
    try {
      projectFrame.getLayerViewPanel().getViewport().zoomToFullExtent();
    } catch (NoninvertibleTransformException e1) {
        e1.printStackTrace();
    }
    
  }
}
