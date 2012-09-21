package fr.ign.cogit.geoxygene.sig3d.io.XML;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;
import fr.ign.cogit.geoxygene.feature.FT_Feature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Solid;

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
 * Permet de sauvgarder un fichier au format XML (ancien bati3D) Format interne
 * donc classe à ne pas trop utiliser ...
 * 
 * This class enables to save a List of feature into the old bati 3D format
 * 
 * 
 */
@Deprecated
public class ExportXML {

  private final static Logger logger = Logger.getLogger(ExportXML.class
      .getName());

  /**
   * Permet de sauvegarder une liste d'objets au format XML
   * 
   * @param featColl
   * @param nomfichier
   */
  public static void export(FT_FeatureCollection<FT_Feature> featColl,
      String nomfichier) {

    try {
      FileWriter data = new FileWriter(nomfichier);

      data.write("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n");
      data.write("<batiments>\n");
      for (int i = 0; i < featColl.size(); i++) {

        FT_Feature obj = featColl.get(i);
        if (obj.getGeom().dimension() != 3) {

          continue;
        }
        data.write("<batiment>\n");
        data.write("<faces>\n");

        List<IOrientableSurface> lFaces = ((GM_Solid) featColl.get(i).getGeom())
            .getFacesList();

        int nbFacettes = lFaces.size();

        for (int j = 0; j < nbFacettes; j++) {

          IOrientableSurface face = lFaces.get(j);

          data.write("<face>\n");
          for (int k = 0; k < face.coord().size(); k++) {
            IDirectPosition pt = face.coord().get(k);
            data.write("<point>\n");
            data.write("<x> " + pt.getX() + " </x> <y> " + pt.getY()
                + " </y> <z> " + pt.getZ() + " </z>\n");
            data.write("</point>\n");
          }
          data.write("</face>\n");
        }
        data.write("</faces>\n");
        data.write("</batiment>\n");

      }
      data.write("</batiments>\n");

      data.close();
      ExportXML.logger.info("Fichier export sauvegardé");

    } catch (IOException e) {
      ExportXML.logger.warn("erreur dans la sauvegarde du fichier");
    }

  }
}
