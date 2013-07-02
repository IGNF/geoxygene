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

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;

import org.apache.log4j.Logger;

import fr.ign.cogit.cartagen.spatialanalysis.network.roads.TronconDeRouteImplWithAttributes;
import fr.ign.cogit.geoxygene.api.feature.type.GF_AttributeType;
import fr.ign.cogit.geoxygene.api.feature.type.GF_FeatureType;
import fr.ign.cogit.geoxygene.schemageo.api.routier.TronconDeRoute;

/*
 * ###### IGN / CartAGen ###### Title: RoadLine Description: Tronçons de route
 * Author: J. Renard Date: 18/09/2009
 */

@Entity
@Access(AccessType.PROPERTY)
public class RoadLineWithAttributes extends RoadLine {

  private static final Logger logger = Logger
      .getLogger(RoadLineWithAttributes.class.getName());

  private Object[] attributes;

  public RoadLineWithAttributes(TronconDeRoute geoxObj, int importance,
      int symbolId) {
    super(geoxObj, importance, symbolId);
    TronconDeRouteImplWithAttributes tr = new TronconDeRouteImplWithAttributes(
        geoxObj, this.attributes);
    this.setGeoxObj(tr);

  }

  public RoadLineWithAttributes(TronconDeRoute geoxObj, int importance) {
    super(geoxObj, importance);

    if (!(geoxObj instanceof TronconDeRouteImplWithAttributes)) {

      TronconDeRouteImplWithAttributes tr = new TronconDeRouteImplWithAttributes(
          geoxObj, this.attributes);

      this.setGeoxObj(tr);
    }
  }

  /**
   * Default constructor, used by Hibernate.
   */
  /*
   * public RoadLineWithAttributes() { super();
   * 
   * }
   */

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

  public Object[] getAttributs() {
    return this.attributes;
  }

}
