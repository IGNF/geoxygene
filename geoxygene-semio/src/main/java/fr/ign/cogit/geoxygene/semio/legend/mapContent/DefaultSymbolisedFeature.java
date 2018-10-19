package fr.ign.cogit.geoxygene.semio.legend.mapContent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import fr.ign.cogit.geoxygene.feature.FT_Feature;
import fr.ign.cogit.geoxygene.semio.legend.symbol.color.Contrast;

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
 * @author Charlotte Hoarau - IGN / Laboratoire COGIT
 *
 */
public class DefaultSymbolisedFeature extends FT_Feature implements SymbolisedFeature {

  /**
   * Average of contrasts of all relationships concerned by this element. 
   */
  private Contrast contrast = new Contrast();
  
  @Override
  public Contrast getContrast() {
    return contrast;
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
    return area;
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
}
