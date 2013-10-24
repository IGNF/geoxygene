/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.core.defaultschema.road;

import java.awt.Color;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

import org.hibernate.annotations.Type;

import fr.ign.cogit.cartagen.core.carto.SLDUtil;
import fr.ign.cogit.cartagen.core.defaultschema.network.NetworkSection;
import fr.ign.cogit.cartagen.core.genericschema.SymbolShape;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkNode;
import fr.ign.cogit.cartagen.core.genericschema.road.IRoadLine;
import fr.ign.cogit.cartagen.software.GeneralisationLegend;
import fr.ign.cogit.cartagen.software.dataset.CartAGenDocOld;
import fr.ign.cogit.cartagen.spatialanalysis.network.roads.RoadLineImpl;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.schemageo.api.routier.TronconDeRoute;
import fr.ign.cogit.geoxygene.schemageo.api.support.reseau.Direction;
import fr.ign.cogit.geoxygene.schemageo.api.support.reseau.NoeudReseau;
import fr.ign.cogit.geoxygene.schemageo.impl.routier.TronconDeRouteImpl;
import fr.ign.cogit.geoxygene.schemageo.impl.support.reseau.ReseauImpl;

/*
 * ###### IGN / CartAGen ###### Title: RoadLine Description: Tronçons de route
 * Author: J. Renard Date: 18/09/2009
 */
@Entity
@Access(AccessType.PROPERTY)
public class RoadLine extends NetworkSection implements IRoadLine {

  /**
   * Associated Geoxygene schema object
   */
  private TronconDeRoute geoxObj;
  private boolean deadEnd;
  private RoadNode initialNode, finalNode;
  private Direction direction;

  /**
   * Constructor
   */
  public RoadLine(TronconDeRoute geoxObj, int importance, int symbolId) {
    super();
    this.geoxObj = geoxObj;
    this.setInitialGeom((IGeometry) geoxObj.getGeom().clone());
    this.setEliminated(false);
    this.setImportance(importance);
    this.deadEnd = false;
    this.initialNode = null;
    this.finalNode = null;
    this.setSymbolId(symbolId);
  }

  public RoadLine(TronconDeRoute geoxObj, int importance) {
    super();
    this.geoxObj = geoxObj;
    this.setInitialGeom(geoxObj.getGeom());
    this.setEliminated(false);
    this.setImportance(importance);
    this.deadEnd = false;
    this.initialNode = null;
    this.finalNode = null;
  }

  public RoadLine(ILineString line, int importance) {
    super();
    this.geoxObj = new TronconDeRouteImpl(new ReseauImpl(), false, line);
    this.setInitialGeom(line);
    this.setEliminated(false);
    this.setImportance(importance);
    this.deadEnd = false;
    this.initialNode = null;
    this.finalNode = null;
  }

  public RoadLine(ILineString line, int importance, int symbolId) {
    super();
    this.geoxObj = new TronconDeRouteImpl(new ReseauImpl(), false, line);
    this.setInitialGeom(line);
    this.setEliminated(false);
    this.setImportance(importance);
    this.setSymbolId(symbolId);
    this.deadEnd = false;
    this.initialNode = null;
    this.finalNode = null;
  }

  public RoadLine(RoadLineImpl geoxObj) {
    super();
    this.geoxObj = geoxObj;
    this.setEliminated(false);
    this.setImportance(geoxObj.getImportance());
    this.deadEnd = false;
    this.initialNode = null;
    this.finalNode = null;
  }

  /**
   * Default constructor, used when the network is made planar.
   */
  public RoadLine(ILineString line) {
    super();
    this.geoxObj = new TronconDeRouteImpl(new ReseauImpl(), false, line);
    this.setInitialGeom(line);
    this.setEliminated(false);
    this.deadEnd = false;
    this.initialNode = null;
    this.finalNode = null;
  }

  /**
   * Default constructor, used by Hibernate.
   */
  public RoadLine() {
    super();
  }

  @Override
  @Transient
  public IFeature getGeoxObj() {
    return this.geoxObj;
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
  @Transient
  public double getWidth() {
    if (this.getSymbolId() == -1) {// old way of calculating the width

      if (this.getImportance() == 0) {
        return GeneralisationLegend.ROUTIER_LARGEUR_DESSOUS_0;
      }
      if (this.getImportance() == 1) {
        return GeneralisationLegend.ROUTIER_LARGEUR_DESSOUS_1;
      }
      if (this.getImportance() == 2) {
        return GeneralisationLegend.ROUTIER_LARGEUR_DESSOUS_2;
      }
      if (this.getImportance() == 3) {
        return GeneralisationLegend.ROUTIER_LARGEUR_DESSOUS_3;
      }
      if (this.getImportance() == 4) {
        return GeneralisationLegend.ROUTIER_LARGEUR_DESSOUS_4;
      }

    } else if (this.getSymbolId() == -2) {// SLD width

      return SLDUtil.getSymbolMaxWidthMapMm(this);

    } else { // the new way of calculating the width
      SymbolShape symbolShape = CartAGenDocOld.getInstance()
          .getCurrentDataset().getSymbols()
          .getSymbolShapeBySymbolID(this.getSymbolId());
      return symbolShape.ext_width;
    }

    return 0.0;
  }

  @Override
  @Transient
  public double getInternWidth() {
    if (this.getSymbolId() == -1) {// old way
      if (this.getImportance() == 0) {
        return GeneralisationLegend.ROUTIER_LARGEUR_DESSUS_0;
      }
      if (this.getImportance() == 1) {
        return GeneralisationLegend.ROUTIER_LARGEUR_DESSUS_1;
      }
      if (this.getImportance() == 2) {
        return GeneralisationLegend.ROUTIER_LARGEUR_DESSUS_2;
      }
      if (this.getImportance() == 3) {
        return GeneralisationLegend.ROUTIER_LARGEUR_DESSUS_3;
      }
      if (this.getImportance() == 4) {
        return GeneralisationLegend.ROUTIER_LARGEUR_DESSUS_4;
      }
    } else if (this.getSymbolId() == -2) {// SLD width

      return SLDUtil.getSymbolInnerWidthMapMm(this);

    } else { // the new way of calculating the width
      SymbolShape symbolShape = CartAGenDocOld.getInstance()
          .getCurrentDataset().getSymbols()
          .getSymbolShapeBySymbolID(this.getSymbolId());
      return symbolShape.int_width;
    }
    return 0.0;
  }

  @Transient
  public double getLargeurSeparateur() {
    if (this.getImportance() == 4) {
      return GeneralisationLegend.ROUTIER_LARGEUR_SEPARATEUR_4;
    }
    return 0.0;
  }

  @Override
  @Transient
  public Color getSeparatorColor() {
    if (this.getImportance() == 4) {
      return GeneralisationLegend.ROUTIER_COULEUR_SEPARATEUR_4;
    }
    return null;
  }

  @Override
  @Transient
  public Color getFrontColor() {
    if (this.getImportance() == 0) {
      return GeneralisationLegend.ROUTIER_COULEUR_0;
    }
    if (this.getImportance() == 1) {
      return GeneralisationLegend.ROUTIER_COULEUR_1;
    }
    if (this.getImportance() == 2) {
      return GeneralisationLegend.ROUTIER_COULEUR_2;
    }
    if (this.getImportance() == 3) {
      return GeneralisationLegend.ROUTIER_COULEUR_3;
    }
    if (this.getImportance() == 4) {
      return GeneralisationLegend.ROUTIER_COULEUR_4;
    }
    return null;
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
    this.finalNode = (RoadNode) node;
  }

  @Override
  public void setInitialNode(INetworkNode node) {
    this.initialNode = (RoadNode) node;
  }

  public void setGeoxObj(TronconDeRoute geoxObj) {
    this.geoxObj = geoxObj;
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

  @Override
  public int getImportance() {
    return super.getImportance();
  }

  @Override
  public void restoreGeoxObjects() {
    this.geoxObj = new TronconDeRouteImpl(new ReseauImpl(), false,
        this.getGeom());
  }

  @Override
  public void restoreGeoxRelations() {
    TronconDeRoute geox = (TronconDeRoute) this.getGeoxObj();
    if (this.getInitialNode() != null) {
      geox.setNoeudInitial((NoeudReseau) this.getInitialNode().getGeoxObj());
      ((NoeudReseau) this.getInitialNode().getGeoxObj()).getArcsSortants().add(
          geox);
    }
    if (this.getFinalNode() != null) {
      geox.setNoeudFinal((NoeudReseau) this.getFinalNode().getGeoxObj());
      ((NoeudReseau) this.getFinalNode().getGeoxObj()).getArcsEntrants().add(
          geox);
    }
  }

}
