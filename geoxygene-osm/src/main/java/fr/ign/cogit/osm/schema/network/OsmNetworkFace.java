package fr.ign.cogit.osm.schema.network;

import java.util.Collection;
import java.util.HashSet;

import fr.ign.cogit.cartagen.core.genericschema.network.INetworkFace;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkSection;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Face;
import fr.ign.cogit.osm.schema.OsmGeneObjSurf;

public class OsmNetworkFace extends OsmGeneObjSurf implements INetworkFace {
  private Face geoxObj;
  private Collection<INetworkSection> sections;

  /**
   * Constructor
   */
  public OsmNetworkFace(Face geoxObj) {
    super();
    this.geoxObj = geoxObj;
    this.setInitialGeom(geoxObj.getGeom());
    this.setEliminated(false);
    this.sections = new HashSet<INetworkSection>();
  }

  public OsmNetworkFace(IPolygon poly) {
    super();
    this.geoxObj = new Face();
    this.geoxObj.setGeom(poly);
    this.setInitialGeom(poly);
    this.setEliminated(false);
    this.sections = new HashSet<INetworkSection>();
  }

  @Override
  public IFeature getGeoxObj() {
    return this.geoxObj;
  }

  @Override
  public Collection<INetworkSection> getSections() {
    return this.sections;
  }

  public void setSections(Collection<INetworkSection> sections) {
    this.sections = sections;
  }

  public void addToSections(INetworkSection section) {
    this.sections.add(section);
  }

}
