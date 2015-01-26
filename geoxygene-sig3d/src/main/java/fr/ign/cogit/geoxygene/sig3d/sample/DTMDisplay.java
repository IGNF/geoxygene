package fr.ign.cogit.geoxygene.sig3d.sample;

import java.awt.Color;

import fr.ign.cogit.geoxygene.sig3d.gui.MainWindow;
import fr.ign.cogit.geoxygene.sig3d.semantic.DTM;
import fr.ign.cogit.geoxygene.sig3d.util.ColorShade;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Envelope;

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
 * @version 0.1
 * 
 *          Classe de démonstration : chargement d'un MNT format .asc
 * 
 *          Demonstration class :How to load a DTM (.asc format)
 * 
 */
public class DTMDisplay {

    // Exemple de chargement de MNT
    public static void main(String[] args) {

        boolean orthoPhoto = true;

        // Chemin du fichier .asc à charger
        // String mntFile = RGE.class.getResource("/demo3D/bdalti/MNT250_L93_FRANCE.ASC").getPath().toString();
        String mntFile = RGE.class.getResource("/demo3D/bdalti/echantillon72/MNT_25M_asc.asc").getPath().toString();
        DTM mnt;
        if (orthoPhoto) {

            // Chemin de l'orthophoto à plaquer
            String imageAPlaquer = RGE.class.getResource(
                    "/demo3D/bdortho/echantillon72/BD_ORTHO_1m/72-2010-0468-6740-LA93-NB.tif").toString();

            
            
            // Emprise de l'orthophoto
            DirectPosition pMinPhoto = new DirectPosition(468000.00, 6736000.00);
            DirectPosition pMaxPhoto = new DirectPosition(472000.00, 6740000.00);
            GM_Envelope env = new GM_Envelope(pMaxPhoto, pMinPhoto);

            // Création du MNT
            // Fichier du MNT, nom de la couche, représentation solide/filaire,
            // exaggération du relief, image à plaquer, emprise
            mnt = new DTM(mntFile, "MNT", true, 1, imageAPlaquer, env);

        } else {
            // Choix du dégradé
            Color[] degrade = ColorShade.YELLOW_BLUE_WHITE;
            // Création du MNT avec le nom du fichier, le nom de la couche, le
            // mode de représentation, l'exaggération et le dégradé
            mnt = new DTM(mntFile, "MNT", false, 1, degrade);
        }

        // On créer l'interface
        MainWindow fVG = new MainWindow();

        // Le MNT implémente l'interface Couche, on peut l'ajouter
        fVG.getInterfaceMap3D().getCurrent3DMap().addLayer(mnt);

    }

}
