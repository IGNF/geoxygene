package fr.ign.cogit.geoxygene.sig3d.sample;

import java.awt.Color;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.sig3d.gui.MainWindow;
import fr.ign.cogit.geoxygene.sig3d.io.vector.ShapeFileLoader;
import fr.ign.cogit.geoxygene.sig3d.representation.toponym.BasicToponym3D;
import fr.ign.cogit.geoxygene.sig3d.semantic.VectorLayer;

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
 * Exemple d'affichage de toponymes à partir d'une couche de villes, l'attribut
 * portant le nom des villes est lu. Exemple d'utilisation d'une représentation
 * non standard
 * 
 * 
 * Rendering of 3D toponyms from a Shapefile
 * 
 */
public class Toponym {

  public static void main(String[] args) {
    // Chemin du fichier .shp à charger
    String shpFile = RGE.class.getResource("/demo3D/villes/villes_t.shp")
        .getPath().toString();
    // On récupère les données et on les passe au format 3D
    // Pas d'attributs indiquées pour ZMin et ZMax, les points auront comme
    // altitude
    FT_FeatureCollection<IFeature> ftColl = ShapeFileLoader.loadingShapeFile(
        shpFile, "", "", true);

    int nbelem = ftColl.size();
    // On affecte une représentation pour chaque entité
    for (int i = 0; i < nbelem; i++) {
      IFeature feat = ftColl.get(i);
      // On récupère le nom de la ville (Attribut PPPTNAME)
      String nom = feat.getAttribute("PPPTNAME").toString();
      // On affecte une représentation
      feat.setRepresentation(new BasicToponym3D(feat, // L'entité
          Color.red, // La couleur
          1, // Coefficient d'opacité
          0, // Rotation suivant l'axe X
          0, // Rotation suivant l'axe Y
          0, // Rotation suivant l'axe Z
          nom, // Texte du toponyme à afficher
          "Arial", // Police du toponyme
          5000, // Taille du texte
          true // La toponyme fera face à la caméra
      // Cette dernière valeur désactive les rotations

      ));

    }
    // Vu que chaque entité à une style on utilise le constructeur standard
    // pour les couches
    VectorLayer c = new VectorLayer(ftColl, "Villes");

    // On créer l'interface
    MainWindow fVG = new MainWindow();

    // On ajoute la couche à la carte courante de l'interface
    fVG.getInterfaceMap3D().getCurrent3DMap().addLayer(c);

  }
}
