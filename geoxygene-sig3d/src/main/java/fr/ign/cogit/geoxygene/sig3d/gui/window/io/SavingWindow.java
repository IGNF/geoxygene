package fr.ign.cogit.geoxygene.sig3d.gui.window.io;

import java.util.ArrayList;

import javax.swing.JOptionPane;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.sig3d.Messages;
import fr.ign.cogit.geoxygene.sig3d.semantic.Layer;
import fr.ign.cogit.geoxygene.sig3d.semantic.Map3D;
import fr.ign.cogit.geoxygene.sig3d.semantic.VectorLayer;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiSurface;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_OrientableSurface;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Solid;
import fr.ign.cogit.geoxygene.util.conversion.ShapefileWriter;

/**
 * 
 *        This software is released under the licence CeCILL
 * 
 *        see LICENSE.TXT
 * 
 *        see <http://www.cecill.info/ http://www.cecill.info/
 * 
 * 
 * 
 * @copyright IGN
 * 
 * @author Brasebin Mickaël
 *  
 * @version 0.1
 * 
 * Cette classe contient les différentes manières de sauvegarde : shapefile or
 * PostGIS format are now available This class contains the different kind of
 * save
 */
public final class SavingWindow {

  /**
   * Permet d'ouvrir une interface et de sauvegarder une couche dans PostGIS
   * 
   * @param map3D la carte dont on va sauvegarder uune couche
   */
  public static void saveLayerPostGIS(Map3D map3D) {

    VectorLayer coucheSelec = SavingWindow.chooseVectorialLayer(map3D);

    if (coucheSelec == null) {
      JOptionPane.showMessageDialog(map3D.getIMap3D(),
          Messages.getString("FenetreChargement.PostGISNoLayer"),
          Messages.getString("FenetreSauvegarde.Titre"),
          JOptionPane.INFORMATION_MESSAGE);
      return;
    }

    (new PostGISStoringWindow(coucheSelec)).setVisible(true);

  }

  /**
   * Permet d'ouvrir une interface et de sauvegarder une couche en Shape3D
   * 
   * @param map3D la carte dont on va sauvegarder une couche
   * @throws CloneNotSupportedException
   */
  public static void saveLayerShapeFile3D(Map3D map3D)
      throws CloneNotSupportedException {
    VectorLayer coucheSelec = SavingWindow.chooseVectorialLayer(map3D);

    if (coucheSelec == null) {
      JOptionPane.showMessageDialog(map3D.getIMap3D(),
          Messages.getString("FenetreChargement.PostGISNoLayer"),
          Messages.getString("FenetreShapeFile3D.Sauvegarde"),
          JOptionPane.INFORMATION_MESSAGE);
      return;
    }

    IFeatureCollection<IFeature> coucheSauvee = new FT_FeatureCollection<IFeature>();

    int nbElem = coucheSelec.size();

    for (int i = 0; i < nbElem; i++) {

      IFeature feat = coucheSelec.get(i);
     

      IGeometry geom = feat.getGeom();

      if (geom instanceof GM_Solid) {

        feat.setGeom(new GM_MultiSurface<GM_OrientableSurface>(
            ((GM_Solid) geom).getFacesList()));
      }
      coucheSauvee.add(feat);
    }

    ShapefileWriter.chooseAndWriteShapefile(coucheSauvee);
  }

  /**
   * Ouvre une interface pour choisi la couche que l'on souhaite sauvegarder
   * 
   * @param map3D
   * @return
   */
  private static VectorLayer chooseVectorialLayer(Map3D map3D) {

    ArrayList<Layer> lCoucheTemp = map3D.getLayerList();

    ArrayList<VectorLayer> lCoucheVecteur = new ArrayList<VectorLayer>();

    int nbCouches = lCoucheTemp.size();

    // On ne récupère que les couches vectorielles
    for (int i = 0; i < nbCouches; i++) {
      Layer cTemp = lCoucheTemp.get(i);

      if (cTemp instanceof VectorLayer) {
        lCoucheVecteur.add((VectorLayer) cTemp);

      }

    }

    nbCouches = lCoucheVecteur.size();

    if (nbCouches < 1) {

      JOptionPane.showMessageDialog(map3D.getIMap3D(),
          Messages.getString("FenetreSauvegarde.Fail"),
          Messages.getString("FenetreSauvegarde.Titre"),
          JOptionPane.INFORMATION_MESSAGE);

      return null;
    }

    Object[] possibleValues = new Object[nbCouches];

    for (int i = 0; i < nbCouches; i++) {
      possibleValues[i] = lCoucheVecteur.get(i).getLayerName();

    }

    Object selectedValue = JOptionPane.showInputDialog(null,
        Messages.getString("Sauvegarde.ChoixCouche") + " : ",
        Messages.getString("BarrePrincipale.SaveFile"),

        JOptionPane.INFORMATION_MESSAGE, null,

        possibleValues, possibleValues[0]);

    if (selectedValue == null) {

      return null;
    }

    return (VectorLayer) map3D.getLayer(selectedValue.toString());
  }

}
