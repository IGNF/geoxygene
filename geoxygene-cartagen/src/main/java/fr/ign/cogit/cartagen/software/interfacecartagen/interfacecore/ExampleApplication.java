/*******************************************************************************
 * This software is released under the licence CeCILL
 *  
 *  see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 *  
 *  see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 *  
 *  @copyright IGN
 ******************************************************************************/
/**
 * @author julien Gaffuri 6 mars 2009
 */
package fr.ign.cogit.cartagen.software.interfacecartagen.interfacecore;

import java.awt.Color;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;

/**
 * Exemple de d'utilisation de l'interface de géoxygene
 * @author julien Gaffuri 6 mars 2009
 * 
 */
public class ExampleApplication {

  /**
   * @param args
   */
  public static void main(String[] args) {

    // construction de fenetre
    GeoxygeneFrame fr = new GeoxygeneFrame();

    // affichage de la fenetre
    fr.setVisible(true);

    // exemples d'ajout d'une couche
    // une couche est definie a partir d'une feature collection. Là, cette
    // couche est vide
    FT_FeatureCollection<IFeature> col1 = new FT_FeatureCollection<IFeature>();
    LoadedLayer couche1 = fr.getLayerManager().addLayer(col1, true);

    // ajout de 2 couches symbolisees (on peut ajouter plusieurs couches
    // symbolisées d'une même couche)

    // une couche avec la symbolisation par defaut
    fr.getLayerManager().addSymbolisedLayer(couche1, Symbolisation.defaut());
    // une couche avec une symbolisation surfacique (en supposant que les objets
    // ont des géométries surfaciques)
    fr.getLayerManager().addSymbolisedLayer(couche1,
        Symbolisation.surface(Color.ORANGE, Color.RED));

    // initialise la position du centre de la vue sur un objet
    if (col1.size() > 0 && col1.get(0).getGeom() != null) {
      fr.getVisuPanel().center(col1.get(0));
    }

    // rafraichissement initial
    fr.getVisuPanel().activate();
    if (fr.getVisuPanel().automaticRefresh) {
      fr.getVisuPanel().activateAutomaticRefresh();
    }
  }

}
