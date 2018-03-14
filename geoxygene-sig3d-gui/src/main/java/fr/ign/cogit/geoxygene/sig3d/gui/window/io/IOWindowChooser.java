package fr.ign.cogit.geoxygene.sig3d.gui.window.io;

import java.io.File;

import javax.swing.JOptionPane;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.sig3d.Messages;
import fr.ign.cogit.geoxygene.sig3d.gui.InterfaceMap3D;
import fr.ign.cogit.geoxygene.sig3d.gui.filter.CITYGMLFilter;
import fr.ign.cogit.geoxygene.sig3d.gui.filter.DTMAscFilter;
import fr.ign.cogit.geoxygene.sig3d.gui.filter.SHPFilter;
import fr.ign.cogit.geoxygene.sig3d.gui.window.representation.DTMWindow;
import fr.ign.cogit.geoxygene.util.conversion.ShapefileReader;

public class IOWindowChooser {

  public static boolean chooseWindows(InterfaceMap3D imp, File f) throws Exception {

    
    
    //CityGML
    CITYGMLFilter cgmfilter = new CITYGMLFilter();

    if (cgmfilter.accept(f)) {
      (new CityGMLLoadingWindow(imp, f.getAbsolutePath())).setVisible(true);
    }
    
    //MNTAsc
    DTMAscFilter mntASC = new DTMAscFilter();

    if (mntASC.accept(f)) {
        (new DTMWindow(f.getAbsolutePath(), imp)).setVisible(true);
      }
    //SHPFilter
    SHPFilter shpFilter = new SHPFilter();
    
    if(shpFilter.accept(f)){
      
      // On recupere le fichier
      IFeatureCollection<IFeature> ftColl = ShapefileReader
          .read(f.getAbsolutePath());
      // On procède aux tests d'usage
      if (ftColl == null) {
          return false;
      }
      
      int dimension = ftColl.get(0).getGeom().coordinateDimension();
      // Indique si la fenêtre suivante est annulée ou non
      boolean isCanceled = false;

      if (dimension == 2) {
        // Géométrie 2D
        Feature2DLoadingWindow f2dlw = new Feature2DLoadingWindow(
            imp, ftColl);
        f2dlw.setVisible(true);
        isCanceled = f2dlw.isCanceled();

      } else {

        // Géométrie 3D, l'utilisateur choisi

        int result = JOptionPane.showConfirmDialog(imp,
            Messages.getString("FenetreChargement.Keep3DGeomQuestion"), //$NON-NLS-1$
            Messages.getString("3DGIS.Loading"), //$NON-NLS-1$
            JOptionPane.YES_NO_OPTION);

        if (result == JOptionPane.YES_OPTION) {
          ShapeFile3DWindow shap = new ShapeFile3DWindow(imp,
              ftColl);
          shap.setVisible(true);
          isCanceled = shap.isCanceled();

        } else if (result == JOptionPane.NO_OPTION) {
          Feature2DLoadingWindow f2dlw = new Feature2DLoadingWindow(
              imp, ftColl);
          f2dlw.setVisible(true);
          isCanceled = f2dlw.isCanceled();
        }
      }
      if (isCanceled) {

        return false;
      }
      ftColl.clear();
    }
    
    
    
    
    
    
    return false;

  }

  public static boolean chooseWindows(InterfaceMap3D imp, String s) throws Exception {

    File f = new File(s);

    if (f.exists()) {
      return chooseWindows(imp, f);
    }
    return false;

  }

}
