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

import org.apache.log4j.Logger;
import org.hibernate.annotations.Type;

import fr.ign.cogit.cartagen.core.carto.SLDUtilCartagen;
import fr.ign.cogit.cartagen.core.defaultschema.network.NetworkSection;
import fr.ign.cogit.cartagen.core.genericschema.SymbolShape;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkNode;
import fr.ign.cogit.cartagen.core.genericschema.road.IRoadLine;
import fr.ign.cogit.cartagen.software.GeneralisationLegend;
import fr.ign.cogit.cartagen.software.dataset.CartAGenDocOld;
import fr.ign.cogit.cartagen.spatialanalysis.network.roads.TronconDeRouteImplWithAttributes;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.type.GF_AttributeType;
import fr.ign.cogit.geoxygene.api.feature.type.GF_FeatureType;
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
public class RoadLineWithAttributes extends NetworkSection implements IRoadLine {

  private static final Logger logger = Logger
      .getLogger(RoadLineWithAttributes.class.getName());

  /**
   * Associated Geoxygene schema object
   */
  private TronconDeRoute geoxObj;
  private boolean deadEnd;
  private RoadNode initialNode, finalNode;
  private Direction direction;
  private Object[] attributes;

  public RoadLineWithAttributes(TronconDeRoute geoxObj, int importance,
      int symbolId) {
    super();
    this.geoxObj = geoxObj;
    this.setInitialGeom((IGeometry) geoxObj.getGeom().clone());
    this.setEliminated(false);
    this.setImportance(importance);
    this.deadEnd = false;
    this.initialNode = null;
    this.finalNode = null;
    this.setSymbolId(symbolId);
    TronconDeRouteImplWithAttributes tr = new TronconDeRouteImplWithAttributes(
        geoxObj, this.attributes);
    this.setGeoxObj(tr);

  }

  public RoadLineWithAttributes(TronconDeRoute geoxObj, int importance) {
    super();
    this.geoxObj = geoxObj;
    this.setInitialGeom(geoxObj.getGeom());
    this.setEliminated(false);
    this.setImportance(importance);
    this.deadEnd = false;
    this.initialNode = null;
    this.finalNode = null;
    if (!(geoxObj instanceof TronconDeRouteImplWithAttributes)) {

      TronconDeRouteImplWithAttributes tr = new TronconDeRouteImplWithAttributes(
          geoxObj, this.attributes);

      this.setGeoxObj(tr);
    }
  }

  /**
   * Default constructor, used by Hibernate.
   */
  public RoadLineWithAttributes() {
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

      return SLDUtilCartagen.getSymbolMaxWidthMapMm(this);

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

      return SLDUtilCartagen.getSymbolInnerWidthMapMm(this);

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

  // public RoadLineWithAttributes

  public Object[] getAttributes() {
    return this.attributes;
  }

  /**
   * Renvoie l'attribut de position <code>n</code> dans le tableau d'attributs
   * @param rang le rang de l'attribut
   * @return l'attribut de position <code>n</code> dans le tableau d'attributs
   */
  public Object getAttribute(int rang) {
    return this.attributes[rang];
  }

  @Override
  public Object getAttribute(String nom) {

    for (int i = 0; i < this.getFeatureType().getFeatureAttributes().size(); i++) {
      GF_AttributeType attType = this.getFeatureType().getFeatureAttributes()
          .get(i);
      if (attType.getMemberName().equals(nom))
        return this.getAttribute(i);

    }
    return null;
  }

  @Override
  public Object getAttribute(GF_AttributeType attribute) {
    return this.getAttribute(attribute.getMemberName());
  }

  /**
   * @param attributes the attributes to set
   */
  public void setAttributes(Object[] attributes) {
    this.attributes = attributes;

    ((TronconDeRouteImplWithAttributes) this.getGeoxObj())
        .setAttributes(attributes);

  }

  /**
   * met la valeur value dans la case rang de la table d'attributs. Pour éviter
   * toute erreur, mieux vaut utiliser setAttribute(String nom, Object value)
   * qui va chercher dans le schema l'emplacement correct de l'attribut.
   * @param rang
   * @param value
   */
  /*
   * private void setAttribute(int rang, Object value) { this.attributes[rang] =
   * value; }
   */

  @Override
  public void setAttribute(GF_AttributeType attType, Object valeur) {

    for (int i = 0; i < this.getFeatureType().getFeatureAttributes().size(); i++) {

      GF_AttributeType attTypeTemp = this.getFeatureType()
          .getFeatureAttributes().get(i);
      if (attTypeTemp.getMemberName().equals(attType.getMemberName())) {
        this.attributes[i] = valeur;
        /*
         * ((TronconDeRouteImplWithAttributes) this.getGeoxObj()).setAttribute(
         * attType, valeur);
         */
        return;
      }

    }

    logger.error("The attribut " + attType.getMemberName() + " is not found");
  }

  public void setAttribute(String nom, Object valeur) {

    for (int i = 0; i < this.getFeatureType().getFeatureAttributes().size(); i++) {

      GF_AttributeType attType = this.getFeatureType().getFeatureAttributes()
          .get(i);
      if (attType.getMemberName().equals(nom)) {
        this.attributes[i] = valeur;
        return;
      }

    }

    logger.error("The attribut " + nom + " is not found");

  }

  @Override
  public void setFeatureType(GF_FeatureType featureType) {
    this.featureType = featureType;
    this.setAttributes(new Object[featureType.getFeatureAttributes().size()]);

    this.getGeoxObj().setFeatureType(featureType);
    ((TronconDeRouteImplWithAttributes) this.getGeoxObj()).setAttributes(this
        .getAttributes());

  }

  @Transient
  public Object[] getAttributs() {
    return this.attributes;
  }

}
