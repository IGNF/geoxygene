package fr.ign.cogit.geoxygene.osm.schema.hydro;

import java.util.Date;

import javax.persistence.Transient;

import fr.ign.cogit.cartagen.core.carto.SLDUtil;
import fr.ign.cogit.cartagen.core.genericschema.hydro.IWaterLine;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkNode;
import fr.ign.cogit.cartagen.software.GeneralisationLegend;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.osm.schema.network.OsmNetworkSection;

public class OsmWaterLine extends OsmNetworkSection implements IWaterLine {
  @Transient
  private INetworkNode initialNode;
  @Transient
  private INetworkNode finalNode;

  public OsmWaterLine(String contributor, IGeometry geom, int id,
      int changeSet, int version, int uid, Date date) {
    super(contributor, geom, id, changeSet, version, uid, date);
    this.setImportance(0);
  }

  public OsmWaterLine(ILineString line) {
    super(line);
    this.setImportance(0);
  }

  @Override
  public double getWidth() {
    if (this.getSymbolId() == -2) {// SLD width
      return SLDUtil.getSymbolMaxWidthMapMm(this);
    }
    return GeneralisationLegend.RES_EAU_LARGEUR;
  }

  @Override
  public double getInternWidth() {
    if (this.getSymbolId() == -2) {// SLD width
      return SLDUtil.getSymbolInnerWidthMapMm(this);
    }
    return GeneralisationLegend.RES_EAU_LARGEUR;
  }

  @Override
  public INetworkNode getInitialNode() {
    return initialNode;
  }

  @Override
  public void setInitialNode(INetworkNode node) {
    this.initialNode = node;
  }

  @Override
  public INetworkNode getFinalNode() {
    return finalNode;
  }

  @Override
  public void setFinalNode(INetworkNode node) {
    this.finalNode = node;
  }

}
