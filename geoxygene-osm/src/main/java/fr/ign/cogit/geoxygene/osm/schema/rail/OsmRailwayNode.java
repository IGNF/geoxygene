package fr.ign.cogit.geoxygene.osm.schema.rail;

import fr.ign.cogit.cartagen.core.genericschema.network.INetworkSection;
import fr.ign.cogit.cartagen.core.genericschema.railway.IRailwayNode;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Noeud;
import fr.ign.cogit.geoxygene.osm.schema.network.OsmNetworkNode;
import fr.ign.cogit.geoxygene.schemageo.api.ferre.NoeudFerre;
import fr.ign.cogit.geoxygene.schemageo.api.support.reseau.ArcReseau;
import fr.ign.cogit.geoxygene.schemageo.impl.ferre.NoeudFerreImpl;

public class OsmRailwayNode extends OsmNetworkNode implements IRailwayNode {

  private NoeudFerre geoxObj;

  /**
   * Constructor
   */
  public OsmRailwayNode(Noeud noeud) {
    super(noeud);
    this.setGeom(noeud.getGeom());
    this.geoxObj = new NoeudFerreImpl();
    this.geoxObj.setGeom(noeud.getGeom());
    for (INetworkSection section : this.getInSections()) {
      // links creation for GeOx objects
      this.geoxObj.getArcsEntrants().add((ArcReseau) section.getGeoxObj());
      ((ArcReseau) section.getGeoxObj()).setNoeudFinal(this.geoxObj);
    }
    for (INetworkSection section : this.getOutSections()) {
      // links creation for GeOx objects
      this.geoxObj.getArcsSortants().add((ArcReseau) section.getGeoxObj());
      ((ArcReseau) section.getGeoxObj()).setNoeudInitial(this.geoxObj);
    }
  }

  public OsmRailwayNode(IPoint point) {
    super(new Noeud(point));
  }

  /**
   * Default constructor, used by Hibernate.
   */
  public OsmRailwayNode() {
    super();
  }

}
