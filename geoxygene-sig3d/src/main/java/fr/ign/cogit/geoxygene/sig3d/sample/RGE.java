package fr.ign.cogit.geoxygene.sig3d.sample;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import fr.ign.cogit.geoxygene.sig3d.gui.MainWindow;
import fr.ign.cogit.geoxygene.sig3d.sample.rge.MNTRGE;
import fr.ign.cogit.geoxygene.sig3d.semantic.VectorLayer;
import fr.ign.cogit.geoxygene.sig3d.util.ColorShade;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Envelope;

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
 * Classe exemple permettant de charger des àchantillons du RGE Class to learn
 * how to load some data from the french referential
 */
public class RGE {
  /**
   * Launcher of the class
   * 
   * @param args
   * @throws URISyntaxException
   */
  public static void main(String[] args) throws URISyntaxException {
    // 2 possibilités avec ou sans ortho
    boolean orthophoto = true;

    // Avec ou sans surechantillonnage
    boolean echantillonnage = true;

    // On récupère le repertoire des données à plaquer
    URL schemaFile = RGE.class.getResource("/demo3D/bdtopo_lam93/");
    String vecteurAplaquer = schemaFile.getPath().toString();

    // On récupère le MNT qui sera affiché
    String mntFile = RGE.class.getResource("/demo3D/bdalti/echantillon38/ISERE_100_asc.asc")
        .getPath().toString();
    // On utilise MNTRGE extension de MNT pour cet exemple
    // Cette classe propose d'autres possibilités de plaquage des données
    MNTRGE mnt;
    
    MNTRGE.CONSTANT_OFFSET = 2;

    if (orthophoto) {
      // Cas ou l'on souhaite l'orthophoto
      String imageAPlaquer = RGE.class.getResource(
          "/demo3D/bdortho/echantillon38/orthophoto.jpg").toString();

      // On récupère l'emprise de l'ortho
      DirectPosition pMinPhoto = new DirectPosition(915500.00, 6453000.00);
      DirectPosition pMaxPhoto = new DirectPosition(920500.00, 6458000.00);
      GM_Envelope env = new GM_Envelope(pMaxPhoto, pMinPhoto);

      // On instancie le mnt
      mnt = new MNTRGE(mntFile, // Chemin du MNT
          "MNT", // Nom de la couche
          true,// Indique que l'on souhaite une représentation remplie
          // ou sous forme de mailles
          1, // Coefficient d'exaggération
          imageAPlaquer,// Chemin de l'image à plaquer
          env // Enveloppe de l'image à plaquer
      );
    } else {
      // Pas d'otho on applique un dégradé
      mnt = new MNTRGE(mntFile, // Chemin du MNT
          "MNT",// Nom de la couche
          true, // Indique que l'on souhaite une représentation
          // remplie ou sous forme de maille
          1, // Exaggeration du relief
          ColorShade.GREEN_BLUE_WHITE // Dégradé à appliquer. Il
      // s'agit
      // un objet de type Color[] la
      // classe Degrade en fournit de
      // base
      );
    }
    // On crée une fenêtre
    MainWindow fVG = new MainWindow();
    fVG.setVisible(true);

    // On affiche le MNT
    fVG.getInterfaceMap3D().getCurrent3DMap().addLayer(mnt);

    // On récupère les chemins des différents shapefile à appliquer
    List<File> lFichiers = RGE.recupSHP(vecteurAplaquer);

    if (lFichiers == null) {
      return;
    }

    int nbCouche = lFichiers.size();

    // On charge les shapes 1 à 1
    for (int i = 0; i < nbCouche; i++) {
      // On récupère le nom de la couche
      // (Nom du fichier sans extension)
      String nomCouche = lFichiers.get(i).getName();
      int pos = nomCouche.lastIndexOf('.');
      nomCouche = nomCouche.substring(0, pos);
      // Placage des Feature
      // Le placage entraine dans ce cas la création d'un style
      VectorLayer coll = mnt.mapShape(lFichiers.get(i).getAbsolutePath(),// Chemin
          // du
          // shapefile
          nomCouche,// nom de la couche
          echantillonnage// avec ou sans sur echantillonnage
          );

      if (coll == null) {
        continue;
      }
      // Si la couche n'a pas d'éléments, on continue
      int nbElem = coll.size();

      if (nbElem == 0) {
        continue;
      }
      // Sinon on ajoute la couche à la carte
      fVG.getInterfaceMap3D().getCurrent3DMap().addLayer(coll);

    }

  }

  /**
   * Petite fonction pour récupèrer les shapefils dans les dossiers d'un dossier
   * 
   * @param nomDossier le nom du dossier dans lequel on effectue la recherche
   * @return renvoie la liste des URL fichiers shapes sous forme de chaines de
   *         caractères
   */
  public static List<File> recupSHP(String nomDossier) {

    List<File> formatsDisponibles = new ArrayList<File>();

    File directoryToScan = new File(nomDossier);
    File[] lf = directoryToScan.listFiles();

    if (lf == null) {
      return null;
    }

    int nbFichiers = lf.length;

    for (int i = 0; i < nbFichiers; i++) {

      File dossier = lf[i];

      File[] files = dossier.listFiles();

      int nbFichiersDansDossier = files.length;

      for (int j = 0; j < nbFichiersDansDossier; j++) {

        File f = files[j];

        String nom = f.getName();

        int pos = nom.lastIndexOf('.');

        if (pos == -1) {

          continue;
        }

        String extension = nom.substring(pos);

        if (extension.equalsIgnoreCase(".SHP")) {
          formatsDisponibles.add(f);

        }
      }

    }

    return formatsDisponibles;
  }

}
