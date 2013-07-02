package fr.ign.cogit.cartagen.core.genericschema.road;

import fr.ign.cogit.cartagen.spatialanalysis.network.roads.RoadLineImpl;
import fr.ign.cogit.geoxygene.api.feature.type.GF_AttributeType;
import fr.ign.cogit.geoxygene.api.feature.type.GF_FeatureType;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.ICurve;
import fr.ign.cogit.geoxygene.schemageo.api.support.reseau.ArcReseau;
import fr.ign.cogit.geoxygene.schemageo.api.support.reseau.Reseau;

public class TronconDeRouteImplWithAttributes extends RoadLineImpl {

  // private Object[] attributes;

  public TronconDeRouteImplWithAttributes(ArcReseau geoxObj, Object[] attributes) {
    super(geoxObj.getReseau(), geoxObj.isFictif(), geoxObj.getGeom());
    // this.setFeatureType(ft);
    this.setAttributes(attributes);

  }

  public TronconDeRouteImplWithAttributes(Reseau res, GF_FeatureType ft,
      Object[] attributes, ICurve geom) {
    super(res, false, geom);
    this.setFeatureType(ft);
    this.setAttributes(attributes);

  }

  /**
   * Renvoie l'attribut de position <code>n</code> dans le tableau d'attributs
   * @param rang le rang de l'attribut
   * @return l'attribut de position <code>n</code> dans le tableau d'attributs
   */
  // @Override
  // public Object getAttribute(int rang) {
  // return this.getA[rang];
  // }

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
  // public void setAttributes(Object[] attributes) {
  // this.attributes = attributes;
  // }

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
  public void setAttribute(String nom, Object valeur) {

    for (int i = 0; i < this.getFeatureType().getFeatureAttributes().size(); i++) {

      GF_AttributeType attType = this.getFeatureType().getFeatureAttributes()
          .get(i);
      if (attType.getMemberName().equals(nom)) {
        this.getAttributes()[i] = valeur;
        return;
      }

    }

    // logger.error("The attribut "+nom+" is not found");

  }

  // super.setAttribute(type, valeur);
  @Override
  public void setAttribute(GF_AttributeType attribute, Object valeur) {
    // TODO Auto-generated method stub
    for (int i = 0; i < this.getFeatureType().getFeatureAttributes().size(); i++) {

      GF_AttributeType attType = this.getFeatureType().getFeatureAttributes()
          .get(i);
      if (attType.getMemberName().equals(attribute.getMemberName())) {
        this.getAttributes()[i] = valeur;
        return;
      }

    }
  }

  @Override
  public void setFeatureType(GF_FeatureType featureType) {
    this.featureType = featureType;
    this.setAttributes(new Object[featureType.getFeatureAttributes().size()]);
  }

}
