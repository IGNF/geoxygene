/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.spatialanalysis.network.roads;

import java.util.HashSet;

import fr.ign.cogit.geoxygene.schemageo.api.routier.NoeudRoutier;
import fr.ign.cogit.geoxygene.schemageo.api.routier.TronconDeRoute;
import fr.ign.cogit.geoxygene.schemageo.api.support.reseau.ArcReseau;

public class StandardCrossRoad extends SimpleCrossRoad {

  public StandardCrossRoad(NoeudRoutier node) {
    super(node.getGeom());
    this.setNode(node);
    this.setDegree(node.getArcsEntrants().size()
        + node.getArcsSortants().size());
    this.setCoord(node.getGeom().getPosition());
    this.setRoads(new HashSet<TronconDeRoute>());
    for (ArcReseau road : node.getArcsEntrants()) {
      this.getRoads().add((TronconDeRoute) road);
    }
    for (ArcReseau road : node.getArcsSortants()) {
      this.getRoads().add((TronconDeRoute) road);
    }
  }
}
