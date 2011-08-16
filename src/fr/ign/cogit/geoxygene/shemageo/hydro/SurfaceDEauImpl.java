/**
 * @author julien Gaffuri 26 juin 2009
 */
package fr.ign.cogit.geoxygene.shemageo.hydro;

import fr.ign.cogit.geoxygene.api.schemageo.hydro.Regime;
import fr.ign.cogit.geoxygene.api.schemageo.hydro.SurfaceDEau;
import fr.ign.cogit.geoxygene.api.schemageo.support.reseau.Reseau;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.FeatureType;
import fr.ign.cogit.geoxygene.shemageo.support.reseau.ElementZonalReseauImpl;

/**
 * lacs, etendues d'eau diverses representees sous forme surfacique
 * 
 * @author julien Gaffuri 26 juin 2009
 * 
 */
public class SurfaceDEauImpl extends ElementZonalReseauImpl implements
    SurfaceDEau {

  public SurfaceDEauImpl() {
    super();
  }

  public SurfaceDEauImpl(Reseau res, IPolygon geom) {
    this();
    this.setReseau(res);
    this.setGeom(geom);
  }

  /**
   * le type
   */
  private FeatureType type;

  public FeatureType getType() {
    return this.type;
  }

  public void setType(FeatureType type) {
    this.type = type;
  }

  /**
   * le nom
   */
  private String nom = "";

  public String getNom() {
    return this.nom;
  }

  public void setNom(String nom) {
    this.nom = nom;
  }

  /**
   * l'altitude moyenne de l'objet
   */
  private double zMoy;

  public double getZMoy() {
    return this.zMoy;
  }

  public void setZMoy(double zMoy) {
    this.zMoy = zMoy;
  }

  /**
   * le regime
   */
  private Regime regime;

  public Regime getRegime() {
    return this.regime;
  }

  public void setRegime(Regime regime) {
    this.regime = regime;
  }

}
