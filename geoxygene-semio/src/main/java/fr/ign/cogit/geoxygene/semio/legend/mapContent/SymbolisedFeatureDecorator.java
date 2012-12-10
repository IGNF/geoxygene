package fr.ign.cogit.geoxygene.semio.legend.mapContent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import fr.ign.cogit.geoxygene.semio.legend.symbol.color.Contrast;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.api.feature.Representation;
import fr.ign.cogit.geoxygene.api.feature.type.GF_AssociationRole;
import fr.ign.cogit.geoxygene.api.feature.type.GF_AttributeType;
import fr.ign.cogit.geoxygene.api.feature.type.GF_FeatureType;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IEnvelope;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.api.spatial.toporoot.ITopology;

/**
 * 
 *        This software is released under the licence CeCILL
 * 
 *        see Licence_CeCILL-C_fr.html
 *        see Licence_CeCILL-C_en.html
 * 
 *        see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * 
 * @copyright IGN
 * 
 * @author Elodie Buard - IGN / Laboratoire COGIT
 * @author Sébastien Mustière - IGN / Laboratoire COGIT
 * @author Vianney Dugrain - IGN / Laboratoire COGIT
 * @author Charlotte Hoarau - IGN / Laboratoire COGIT
 *
 * A feature in the map.
 * Un objet de la carte.
 *  
 */
public class SymbolisedFeatureDecorator implements SymbolisedFeature {
  IFeature feature;
  
  public SymbolisedFeatureDecorator(IFeature f) {
    this.feature = f;
  }
  
  /**
   * Average of contrasts of all relationships concerned by this element. 
   */
  private Contrast contrast = new Contrast();
  
  @Override
  public Contrast getContrast() {
    return this.contrast;
  }

  @Override
  public void setContrast(Contrast contrast) {
    this.contrast = contrast;
  }

  /**
   * Area of the feature on the map. 
   * 
   * <strong>French:</strong><br />
   * Superficie de l'objet sur la carte.
   * Unité: unité "terrain" (-> mêtres carrés en général)
   * - Si l'objet est un polygone : superficie = aire(polygone)
   * - Si l'objet est un point : superficie = aire(cercle de rayon égal à la largeur du symbole)
   * - Si l'objet est une ligne: superficie = longueur * largeur du symbole
   *  
   * Valeur égale à -1 par défaut avant intialisation
   */
  private double area = -1;
  
  @Override
  public double getArea() {
    return this.area;
  }

  @Override
  public void setArea(double area) {
    this.area = area;
  }

  /**
   * Feature collection containing this feature. 
   */
  private SymbolisedFeatureCollection symbolisedFeatureCollection;
  
  @Override
  public SymbolisedFeatureCollection getSymbolisedFeatureCollection() {
    return this.symbolisedFeatureCollection;
  }

  @Override
  public void setSymbolisedFeatureCollection(SymbolisedFeatureCollection O) {
    SymbolisedFeatureCollection old = this.symbolisedFeatureCollection;
    this.symbolisedFeatureCollection = O;
    if ( old != null ) {
      old.remove(this);
    }
    if ( O != null) {
        if ( ! O.contains(this) ) {
          O.add(this);
        }
    }
  }

  /**
   * List of neighborhood relationships concerning this feature.
   */
  private List<NeighbohoodRelationship> neighborhoodRelationships =
      new ArrayList<NeighbohoodRelationship>();
  
  @Override
  public List<NeighbohoodRelationship> getNeighborhoodRelationships() {
    return this.neighborhoodRelationships ;
  }

  @Override
  public void setNeighborhoodRelationships(
      List<NeighbohoodRelationship> relationships) {
    Iterator<NeighbohoodRelationship> it2 = relationships.iterator();
    while ( it2.hasNext() ) {
        NeighbohoodRelationship O = it2.next();
        this.neighborhoodRelationships.add(O);
        O.getSymbolisedFeatures().add(this);
    }
  }

  @Override
  public void addNeighborhoodRelationship(NeighbohoodRelationship O) {
    if ( O == null ) {
      return;
    }
    this.neighborhoodRelationships.add(O) ;
    O.getSymbolisedFeatures().add(this);
  }

  @Override
  public void removeNeighborhoodRelationship(NeighbohoodRelationship O) {
    if ( O == null ) {
      return;
    }
    this.neighborhoodRelationships.remove(O) ; 
    O.getSymbolisedFeatures().remove(this);
  }
  
  @Override
  public int getId() {
    return this.feature.getId();
  }

  @Override
  public void setId(int Id) {
    this.feature.setId(Id);
  }

  @Override
  public IGeometry getGeom() {
    return this.feature.getGeom();
  }

  @Override
  public void setGeom(IGeometry g) {
    this.feature.setGeom(g);
  }

  @Override
  public boolean hasGeom() {
    return this.feature.hasGeom();
  }

  @Override
  public ITopology getTopo() {
    return this.feature.getTopo();
  }

  @Override
  public void setTopo(ITopology t) {
    this.feature.setTopo(t);
  }

  @Override
  public boolean hasTopo() {
    return this.feature.hasTopo();
  }

  @Override
  public IFeature cloneGeom() throws CloneNotSupportedException {
    return this.feature.cloneGeom();
  }

  @Override
  public List<IFeatureCollection<IFeature>> getFeatureCollections() {
    return this.feature.getFeatureCollections();
  }

  @Override
  public IFeatureCollection<IFeature> getFeatureCollection(int i) {
    return this.feature.getFeatureCollection(i);
  }

  @Override
  public List<IFeature> getCorrespondants() {
    return this.feature.getCorrespondants();
  }

  @Override
  public void setCorrespondants(List<IFeature> L) {
    this.feature.setCorrespondants(L);
  }

  @Override
  public IFeature getCorrespondant(int i) {
    return this.feature.getCorrespondant(i);
  }

  @Override
  public void addCorrespondant(IFeature O) {
    this.feature.addCorrespondant(O);
  }

  @Override
  public void removeCorrespondant(IFeature O) {
    this.feature.removeCorrespondant(O);
  }

  @Override
  public void clearCorrespondants() {
    this.feature.clearCorrespondants();
  }

  @Override
  public void addAllCorrespondants(Collection<IFeature> c) {
    this.feature.addAllCorrespondants(c);
  }

  @Override
  public Collection<IFeature> getCorrespondants(
      IFeatureCollection<? extends IFeature> pop) {
    return this.feature.getCorrespondants(pop);
  }

  @Override
  public IPopulation<? extends IFeature> getPopulation() {
    return this.feature.getPopulation();
  }

  @Override
  public void setFeatureType(GF_FeatureType featureType) {
    this.feature.setFeatureType(featureType);   
  }

  @Override
  public GF_FeatureType getFeatureType() {
    return this.feature.getFeatureType();
  }

  @Override
  public Object getAttribute(GF_AttributeType attribute) {
    return this.feature.getAttribute(attribute);
  }

  @Override
  public void setAttribute(GF_AttributeType attribute, Object valeur) {
    this.feature.setAttribute(attribute, valeur);
  }

  @Override
  public List<? extends IFeature> getRelatedFeatures(GF_FeatureType ftt,
      GF_AssociationRole role) {
    return this.feature.getRelatedFeatures(ftt, role);
  }

  @Override
  public Object getAttribute(String nomAttribut) {
    return this.feature.getAttribute(nomAttribut);
  }

  @Override
  public List<? extends IFeature> getRelatedFeatures(String nomFeatureType,
      String nomRole) {
    return this.feature.getRelatedFeatures(nomFeatureType, nomRole);
  }

  @Override
  public Representation getRepresentation() {
    return this.feature.getRepresentation();
  }

  @Override
  public void setRepresentation(Representation rep) {
    this.feature.setRepresentation(rep);
  }

  @Override
  public boolean isDeleted() {
    return this.feature.isDeleted();
  }

  @Override
  public void setDeleted(boolean deleted) {
    this.feature.setDeleted(deleted);
  }

  @Override
  public boolean intersecte(IEnvelope env) {
    return this.feature.intersecte(env);
  }
  @Override
  public void setPopulation(IPopulation<? extends IFeature> population) {
    this.feature.setPopulation(population);
  }
}
