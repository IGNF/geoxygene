package fr.ign.cogit.geoxygene.sig3d.sample;

import java.net.URL;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.sig3d.gui.MainWindow;
import fr.ign.cogit.geoxygene.sig3d.representation.symbol.Symbol3D;
import fr.ign.cogit.geoxygene.sig3d.semantic.Map3D;
import fr.ign.cogit.geoxygene.sig3d.semantic.VectorLayer;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;

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
 * Exemple proposant un affichage non-standard à des éléments ponctuels :
 * utilisation de symboles pour reprénseter les différents points. Une série de
 * points aléatoire sera générée. Une représentation spécifique sera utilisée
 * pour chaque point
 * 
 * Use of modelling objets to reprensent 0D geometries
 */
public class Symbology {

  // Nombre d'éléments à afficher
  public static int nbElement = 100;
  // coordonnées maximales
  public static int xmax = 10000;
  public static int ymax = 10000;
  public static int zmax = 10000;

  public static void main(String[] Args) throws Exception {

    // On crée l'interface et on récupère l'objet Carte 3D associé
    MainWindow fenPrincipale = new MainWindow();
    Map3D carte = fenPrincipale.getInterfaceMap3D().getCurrent3DMap();

    // On créé la collection d'objets à afficher (100 éléments ici)
    FT_FeatureCollection<IFeature> featCollec = new FT_FeatureCollection<IFeature>();

    for (int i = 0; i < Symbology.nbElement; i++) {

      // On prend un point au hasard dont les coordonnées varient
      // entre 0 et 100
      DirectPosition dp = new DirectPosition(Math.random() * Symbology.xmax,
          Math.random() * Symbology.ymax, Math.random() * Symbology.zmax);

      // ObjetGeographique est juste une implémentation de FT_Feautre
      // Elle n'a qu'un constructeur avec une géométrie.
      featCollec.add(new DefaultFeature(new GM_Point(dp)));
    }

    URL url = Symbology.class.getResource("/texture/eau.jpg");
    // On récupère le chemin du fichier
    // String path = url.getPath().toString();
    // On génère pour chaque élément la représentation que l'on souhaite

    for (int i = 0; i < Symbology.nbElement; i++) {
      IFeature feat = featCollec.get(i);

      feat.setRepresentation(new Symbol3D(feat, 4, url.getPath()));
      /*
       * feat.setRepresentation(new RepresentationModel(feat, // L'entité qui //
       * aura une // nouvelle // représentation path, // L'objet Java3D qui le
       * représentera Math.PI * i / nbElement, // Rotation suivant X 0, //
       * Rotation suivant Y 0, // Rotation suivant Z i / 50// Taille de l'objet
       * ));
       */
    }

    // On crée la couche (les entités ayant une représentation on utilise ce
    // constructeur)
    VectorLayer couche = new VectorLayer(featCollec, "Liste points");
    // On ajoute la couche à la carte
    carte.addLayer(couche);

  }

}
