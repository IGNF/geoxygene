package fr.ign.cogit.geoxygene.sig3d.process;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.sig3d.semantic.DTM;
import fr.ign.cogit.geoxygene.sig3d.util.ColorShade;
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
 * @version 1.6
 * 
 * Class to map vector layer on dtm
 * 
 **/
public class CalculZShape {

  /**
   * @param inputAsc  .asc file that contain a DTM
   * @param inputShape .shp 2D vectorlayer
   * @param outputShape .shp 3D vectorlayer mapped on the DTM
   */
  public static void calcul(String inputAsc, String inputShape,
      String outputShape) {

    // Instanciation du MNT
    DTM mnt = new DTM(inputAsc,// Fichier à charger
        "MNT",// Nom de la couche (le MNT est fait pour être chargé)
        true, // option d'affichage (inutile dans ce cas, sert à indiquer si
              // l'on veut un
        1, // Le coefficient d'exaggération du MNT
        ColorShade.BLUE_PURPLE_WHITE// La couleur du dégradé
    );

    // Plaquage du shapefile
    IFeatureCollection<IFeature> ftCollExp = mnt.mapShapeFile(inputShape, // On
                                                                          // indique
                                                                          // le
                                                                          // nom
                                                                          // du
                                                                          // shape
                                                                          // en
                                                                          // entrée
        false); // Indique si l'on surechantillonne ou pas

    // Ecriture du shape en sortie
    ShapefileWriter.write(ftCollExp, // Nom de la collection à écrire
        outputShape);// Nom du fichier de sortie
  }
}
