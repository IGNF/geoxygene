/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.geoxygene.osm.schema.roads;

import java.awt.Color;

import javax.persistence.Transient;

import fr.ign.cogit.cartagen.core.carto.SLDUtilCartagen;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkFace;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkNode;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkSection;
import fr.ign.cogit.cartagen.core.genericschema.network.NetworkSectionType;
import fr.ign.cogit.cartagen.core.genericschema.road.IPathLine;
import fr.ign.cogit.cartagen.software.GeneralisationLegend;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.osm.schema.OsmGeneObjLin;
import fr.ign.cogit.geoxygene.schemageo.api.support.reseau.ArcReseau;
import fr.ign.cogit.geoxygene.schemageo.api.support.reseau.Direction;
import fr.ign.cogit.geoxygene.schemageo.impl.support.reseau.ArcReseauImpl;

public class OsmPathLine extends OsmGeneObjLin implements IPathLine {

  private int importance;
  private Direction direction;
  private NetworkSectionType netSectionType;
  private boolean deadEnd;
  private ArcReseau geoxObj;
  private INetworkNode initialNode, finalNode;

  /**
   * Constructor
   */
  public OsmPathLine(ILineString line, int importance, int symbolId) {
    super();
    this.geoxObj = new ArcReseauImpl(null, false, line);
    this.setInitialGeom(line);
    this.setEliminated(false);
    this.setImportance(importance);
    this.deadEnd = false;
    this.initialNode = null;
    this.finalNode = null;
    this.setSymbolId(symbolId);
  }

  /**
   * Constructor
   */
  public OsmPathLine(ILineString line, int importance) {
    super();
    this.geoxObj = new ArcReseauImpl(null, false, line);
    this.setInitialGeom(line);
    this.setEliminated(false);
    this.setImportance(importance);
    this.deadEnd = false;
    this.initialNode = null;
    this.finalNode = null;
  }

  @Override
  @Transient
  public IFeature getGeoxObj() {
    return this.geoxObj;
  }

  @Override
  public Color getFrontColor() {
    if (this.importance == 0) {
      return Color.DARK_GRAY;
    }
    return Color.BLACK;
  }

  @Override
  public int getImportance() {
    return this.importance;
  }

  @Override
  public void setImportance(int importance) {
    this.importance = importance;
  }

  @Override
  public Direction getDirection() {
    return this.direction;
  }

  @Override
  public INetworkNode getFinalNode() {
    return this.finalNode;
  }

  @Override
  public INetworkNode getInitialNode() {
    return this.initialNode;
  }

  @Override
  public double getInternWidth() {
    if (this.getSymbolId() == -1) {
      return GeneralisationLegend.ROUTIER_LARGEUR_DESSOUS_0;
    }
    if (this.getSymbolId() == -2)
      return SLDUtilCartagen.getSymbolMaxWidthMapMm(this);
    return 0.0;
  }

  @Override
  public INetworkFace getLeftFace() {
    return null;
  }

  @Override
  public NetworkSectionType getNetworkSectionType() {
    return this.netSectionType;
  }

  @Override
  public INetworkFace getRightFace() {
    return null;
  }

  @Override
  public double getWidth() {
    return getInternWidth();
  }

  @Override
  public boolean isAnalog(INetworkSection at) {
    return false;
  }

  @Override
  public boolean isDeadEnd() {
    return this.deadEnd;
  }

  @Override
  public void setDeadEnd(boolean deadEnd) {
    this.deadEnd = deadEnd;
  }

  @Override
  public void setDirection(Direction direction) {
    this.direction = direction;
  }

  @Override
  public void setFinalNode(INetworkNode node) {
    this.finalNode = node;
  }

  @Override
  public void setInitialNode(INetworkNode node) {
    this.initialNode = node;
  }

  @Override
  public void setNetworkSectionType(NetworkSectionType type) {
    this.netSectionType = type;
  }

}
