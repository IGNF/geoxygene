package fr.ign.cogit.geoxygene.semio.legend.mapContent;

import java.util.List;

import fr.ign.cogit.geoxygene.semio.legend.symbol.color.Contrast;
import fr.ign.cogit.geoxygene.api.feature.IFeature;

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
public interface SymbolisedFeature extends IFeature {
  /**
   * Returns the average of contrasts of all relationships concerned by this element.
   */ 
  public Contrast getContrast();
  
  /**
   * Specifies the average of contrasts of all relationships concerned by this element.
   */ 
  public void setContrast(Contrast contrast);
  
  /**
   * Area of the feature on the map. 
   * 
   * <strong>French:</strong><br />
   * Renvoie la superficie de l'objet sur la carte.
   * Unité: unité "terrain" (-> mêtres carrés en général)
   * - Si l'objet est un polygone : superficie = aire(polygone)
   * - Si l'objet est un point : superficie = aire(cercle de rayon égal à la largeur du symbole)
   * - Si l'objet est une ligne: superficie = longueur * largeur du symbole
   */
  public double getArea();
  
  /**
   * Area of the feature on the map. 
   * 
   * <strong>French:</strong><br />
   * Définit la superficie de l'objet sur la carte.
   * Unité: unité "terrain" (-> mêtres carrés en général)
   * - Si l'objet est un polygone : superficie = aire(polygone)
   * - Si l'objet est un point : superficie = aire(cercle de rayon égal à la largeur du symbole)
   * - Si l'objet est une ligne: superficie = longueur * largeur du symbole
   */
  public void setArea(double superficie);
  
  /**
   * Defines the feature collection containing this feature.
   */
  public SymbolisedFeatureCollection getSymbolisedFeatureCollection();
  
  /**
   * Specifies the feature collection containing this feature.
   */
  public void setSymbolisedFeatureCollection(SymbolisedFeatureCollection O);
  
  /**
   * Returns the list of neighborhood relationships concerning this feature.
   */
  public List<NeighbohoodRelationship> getNeighborhoodRelationships();
  
  /**
   * Specifies the neighborhood relationships concerning this feature.
   */
  public void setNeighborhoodRelationships(List<NeighbohoodRelationship> relationships);
 
  /**
   * Adds an element to the neighborhood relationships concerning this feature.
   */
  public void addNeighborhoodRelationship (NeighbohoodRelationship O);
  
  /**
   * Removes an element from the neighborhood relationships concerning this feature.
   */
  public void removeNeighborhoodRelationship(NeighbohoodRelationship O);
}
