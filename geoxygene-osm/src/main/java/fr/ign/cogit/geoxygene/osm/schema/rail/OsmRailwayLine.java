/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.geoxygene.osm.schema.rail;

import java.util.Map;

import javax.persistence.Transient;

import fr.ign.cogit.cartagen.core.carto.SLDUtilCartagen;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkNode;
import fr.ign.cogit.cartagen.core.genericschema.railway.IRailwayLine;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.osm.schema.network.OsmNetworkSection;
import fr.ign.cogit.geoxygene.schemageo.api.ferre.TronconFerre;
import fr.ign.cogit.geoxygene.schemageo.impl.ferre.TronconFerreImpl;
import fr.ign.cogit.geoxygene.schemageo.impl.support.reseau.ReseauImpl;

public class OsmRailwayLine extends OsmNetworkSection implements IRailwayLine {

  public static final Class<?> associatedNodeClass = OsmRailwayNode.class;

  /**
   * Associated Geoxygene schema object
   */
  private TronconFerre geoxObj;
  private Boolean sidetrack = null;
  private OsmRailwayNode initialNode, finalNode;

  /**
   * Constructor
   */
  public OsmRailwayLine(TronconFerre geoxObj, int importance) {
    super();
    this.geoxObj = geoxObj;
    this.setInitialGeom(geoxObj.getGeom());
    this.setEliminated(false);
    this.setImportance(importance);
  }

  public OsmRailwayLine(ILineString line) {
    super();
    this.geoxObj = new TronconFerreImpl(new ReseauImpl(), false, line);
    this.geoxObj.setId(getId());
    this.setInitialGeom(line);
    this.setEliminated(false);
  }

  @Override
  @Transient
  public IFeature getGeoxObj() {
    return this.geoxObj;
  }

  @Override
  public double getWidth() {
    return SLDUtilCartagen.getSymbolMaxWidthMapMm(this);
  }

  @Override
  public double getInternWidth() {
    return SLDUtilCartagen.getSymbolInnerWidthMapMm(this);
  }

  @Override
  public INetworkNode getInitialNode() {
    return initialNode;
  }

  @Override
  public void setInitialNode(INetworkNode node) {
    this.initialNode = (OsmRailwayNode) node;
  }

  @Override
  public INetworkNode getFinalNode() {
    return finalNode;
  }

  @Override
  public void setFinalNode(INetworkNode node) {
    this.finalNode = (OsmRailwayNode) node;
  }

  @Override
  public void setSidetrack(Boolean sidetrack) {
    this.sidetrack = sidetrack;
  }

  @Override
  public boolean isSidetrack() {
    if (sidetrack == null)
      computeSideTrack();
    return sidetrack;
  }

  @Override
  public void setTags(Map<String, String> tags) {
    super.setTags(tags);
    if (getTags().containsKey("service"))
      this.sidetrack = true;
    if (getTags().containsKey("name"))
      this.geoxObj.setNom(getTags().get("name"));
  }

  /**
   * Computes the value of sidetrack field, using the tags.
   * 
   */
  private void computeSideTrack() {
    if (getTags().containsKey("service")) {
      this.sidetrack = true;
      return;
    }
    if (getTags().containsKey("usage")) {
      this.sidetrack = false;
      return;
    }
    if (getTags().containsKey("name")) {
      this.sidetrack = false;
      return;
    }

    this.sidetrack = false;
  }
}
