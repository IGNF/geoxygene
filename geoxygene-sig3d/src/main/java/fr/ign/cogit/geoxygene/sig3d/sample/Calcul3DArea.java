package fr.ign.cogit.geoxygene.sig3d.sample;

import fr.ign.cogit.geoxygene.sig3d.gui.MainWindow;
import fr.ign.cogit.geoxygene.sig3d.semantic.DTMArea;
import fr.ign.cogit.geoxygene.sig3d.semantic.VectorLayer;
import fr.ign.cogit.geoxygene.sig3d.util.ColorShade;

public class Calcul3DArea {

  /**
   * 
   * This software is released under the licence CeCILL
   * 
   * see LICENSE.TXT
   * 
   * see <http://www.cecill.info/ http://www.cecill.info/
   * 
   * 
   * 
   * @copyright IGN
   * 
   * @author Brasebin Mickaël
   * 
   * @version 1.6
   * 
   *          Sample class that calculates 3D area of a VectorLayer and display
   *          it
   * 
   * 
   **/
  public static void main(String[] args) throws Exception {

    // On récupère le MNT qui sera affiché
    String mntFile = Calcul3DArea.class
        .getResource("/demo3D/mnt/ISERE_100_asc.asc").getPath().toString();

    String inputShape = Calcul3DArea.class
        .getResource("/demo3D/bdtopo_lam93/H_ADMINISTRATIF/COMMUNE.SHP")
        .getPath().toString();

    double area3D = Calcul3DArea.calcul(mntFile, inputShape);

    System.out.println("Area 3D : " + area3D);
  }

  /**
   * @param inputAsc fichier .asc en entrée
   * @param inputShape fichier shape en entrée
   * @throws Exception
   */
  public static double calcul(String inputAsc, String inputShape)
      throws Exception {

    // Instanciation du MNT
    DTMArea mnt = new DTMArea(inputAsc,// Fichier à charger
        "MNT",// Nom de la couche (le MNT est fait pour être chargé)
        true, // option d'affichage (inutile dans ce cas, sert à
              // indiquer si l'on veut un
        1, // Le coefficient d'exaggération du MNT
        ColorShade.BLUE_PURPLE_WHITE// La couleur du dégradé
    );

    // Plaquage du shapefile
    VectorLayer cv = new VectorLayer(mnt.mapShapeFile(inputShape, // On
                                                                  // indique
                                                                  // le nom
                                                                  // du
                                                                  // shape
                                                                  // en
                                                                  // entrée

        true)

    , "Couche_plaquée",

    false, null, 1, true

    ); // Indique si l'on surechantillonne ou pas

    double aire3D = mnt.calcul3DArea(cv);

    System.out.println("Aire3D " + aire3D);

    MainWindow fVG = new MainWindow();
    fVG.setVisible(true);

    // On affiche le MNT
    fVG.getInterfaceMap3D().getCurrent3DMap().addLayer(mnt);

    // On affiche la couche
    fVG.getInterfaceMap3D().getCurrent3DMap().addLayer(cv);

    return aire3D;
  }

}
