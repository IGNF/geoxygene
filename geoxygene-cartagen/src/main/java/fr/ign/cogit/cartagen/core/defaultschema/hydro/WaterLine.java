/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.core.defaultschema.hydro;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

import org.hibernate.annotations.Type;

import fr.ign.cogit.cartagen.core.carto.SLDUtilCartagen;
import fr.ign.cogit.cartagen.core.defaultschema.network.NetworkSection;
import fr.ign.cogit.cartagen.core.genericschema.hydro.IWaterLine;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkNode;
import fr.ign.cogit.cartagen.software.GeneralisationLegend;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.schemageo.api.hydro.TronconHydrographique;
import fr.ign.cogit.geoxygene.schemageo.api.support.reseau.Direction;
import fr.ign.cogit.geoxygene.schemageo.impl.hydro.TronconHydrographiqueImpl;
import fr.ign.cogit.geoxygene.schemageo.impl.support.reseau.ReseauImpl;

/*
 * ###### IGN / CartAGen ###### Title: WaterLine Description: Tronçons
 * hydrographiques Author: J. Renard Date: 18/09/2009
 */
@Entity
@Access(AccessType.PROPERTY)
public class WaterLine extends NetworkSection implements IWaterLine {

  public static final Class<?> associatedNodeClass = WaterNode.class;

  /**
   * Associated Geoxygene schema object
   */
  @Transient
  private TronconHydrographique geoxObj;
  @Transient
  private INetworkNode initialNode;
  @Transient
  private INetworkNode finalNode;
  private Direction direction;
  private boolean deadEnd = false;
  private String name = ""; //$NON-NLS-1$

  /**
   * Empty constructor used by EJB to load features from PostGIS
   */
  public WaterLine() {
    super();
  }

  /**
   * Constructor
   */
  public WaterLine(TronconHydrographique geoxObj, int importance) {
    super();
    this.geoxObj = geoxObj;
    this.name = geoxObj.getNom();
    this.setInitialGeom(geoxObj.getGeom());
    this.setEliminated(false);
    this.setImportance(importance);
  }

  public WaterLine(ILineString line, int importance) {
    super();
    this.geoxObj = new TronconHydrographiqueImpl();
    this.geoxObj.setGeom(line);
    this.setInitialGeom(line);
    this.setEliminated(false);
    this.setImportance(importance);
  }

  @Override
  @Transient
  public IFeature getGeoxObj() {
    return this.geoxObj;
  }

  @Override
  @Transient
  public double getWidth() {
    if (this.getSymbolId() == -2) {// SLD width
      return SLDUtilCartagen.getSymbolMaxWidthMapMm(this);
    }
    return GeneralisationLegend.RES_EAU_LARGEUR;
  }

  @Override
  @Transient
  public double getInternWidth() {
    if (this.getSymbolId() == -2) {// SLD width
      return SLDUtilCartagen.getSymbolInnerWidthMapMm(this);
    }
    return GeneralisationLegend.RES_EAU_LARGEUR;
  }

  @Override
  public Direction getDirection() {
    return this.direction;
  }

  @Override
  @Transient
  public INetworkNode getFinalNode() {
    return this.finalNode;
  }

  @Override
  @Transient
  public INetworkNode getInitialNode() {
    return this.initialNode;
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
  public boolean isDeadEnd() {
    return this.deadEnd;
  }

  @Override
  public void setDeadEnd(boolean deadEnd) {
    this.deadEnd = deadEnd;
  }

  @Override
  @Type(type = "fr.ign.cogit.cartagen.core.persistence.GeOxygeneGeometryUserType")
  public ILineString getGeom() {
    return super.getGeom();
  }

  /**
   * 
   * {@inheritDoc}
   * <p>
   * 
   */
  @Override
  @Column(name = "CartAGenDB_name")
  public String getDbName() {
    return super.getDbName();
  }

  @Override
  @Id
  public int getId() {
    return super.getId();
  }

  @Override
  public int getSymbolId() {
    return super.getSymbolId();
  }

  @Override
  public boolean isEliminated() {
    return super.isEliminated();
  }

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public void restoreGeoxObjects() {
    this.geoxObj = new TronconHydrographiqueImpl(new ReseauImpl(), false,
        this.getGeom());
    this.geoxObj.setNom(this.name);
  }

}
