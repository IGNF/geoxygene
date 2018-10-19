package fr.ign.cogit.geoxygene.semio.legend.mapContent.operation;

import java.util.Collection;

import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.semio.legend.mapContent.NeighbohoodRelationship;
import fr.ign.cogit.geoxygene.semio.legend.mapContent.SymbolisedFeature;
import fr.ign.cogit.geoxygene.util.index.Tiling;

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
 * @author Charlotte Hoarau
 * 
 * Neighborhood Relationship Builder
 * 
 * @see NeighbohoodRelationship
 */

public class NRBuilder {
  
  private SymbolisedFeature symbolisedFeature;
  
  public SymbolisedFeature getSymbolisedFeature() {
    return symbolisedFeature;
  }

  public void setSymbolisedFeature(SymbolisedFeature symbolisedFeature) {
    this.symbolisedFeature = symbolisedFeature;
  }

  private FT_FeatureCollection<SymbolisedFeature> symboFC;
  
  public FT_FeatureCollection<SymbolisedFeature> getSymboFC() {
    return symboFC;
  }
  
  public void setSymboFC(FT_FeatureCollection<SymbolisedFeature> symboFC) {
    this.symboFC = symboFC;
  }
  
  private double radius;
  
  public double getRadius() {
    return radius;
  }
  
  public void setRadius(double radius) {
    this.radius = radius;
  }
  
  private int type;
  
  public int getType() {
    return type;
  }
  
  public void setType(int type) {
    this.type = type;
  }
  
  private boolean order;
  
  public boolean isOrder() {
    return order;
  }
  
  public void setOrder(boolean order) {
    this.order = order;
  }
  
  public NRBuilder(){}

  public NRBuilder(SymbolisedFeature symbolisedFeature,
      FT_FeatureCollection<SymbolisedFeature> symboFC,
      double radius, int type, boolean order) {
    super();
    this.symbolisedFeature = symbolisedFeature;
    this.symboFC = symboFC;
    this.radius = radius;
    this.type = type;
    this.order = order;
  }
  
  /**
   * Search all neighbors of the feature on the map. 
   * 
   * <strong>French:</strong><br />
   * Détermine les voisins d'un objet carto, et crée les relations de voisinage correspondantes.
   * Les voisins sont les objets qui sont proches.
   * 
   * @param tailleVoisinage : seuil de distance entre objets pour les considérer voisins.
   * 
   * @param type :  type des relation à créer (ordre, association, dissociation).
   *  
   * @param ordre : utilisé en cas de relation d'ordre uniquement... 
   *          true: this est plus faible que les objetsCarto en paramêtre
   *          false: this est plus fort que les objetsCarto en paramêtre
   *  
   * @param symbolisedFeatureCollection
   */
  public void buildNR(){
 // Initialisation de l'indexation spatiale au besoin
    // NB: il y a un index par famille
    if (!this.symboFC.hasSpatialIndex()) {
      this.symboFC.initSpatialIndex(Tiling.class, true, 10);
    }
    
    // initialisation de la superficie de l'objet au besoin
    if (this.symbolisedFeature.getArea() == -1) {
      AreaOp.computeArea(this.symbolisedFeature);
    }

    // Objets qui intersectent la géométrie de self
    Collection<SymbolisedFeature> voisins =
        this.symboFC.select(this.symbolisedFeature.getGeom(), this.radius);
    voisins.remove(this);
    
    // creation d'un nouveau lien pour chaque voisin
    for (SymbolisedFeature voisin : voisins) {
        // initialisation de la superficie de l'objet au besoin
        if (this.symbolisedFeature.getArea() == -1) {
          AreaOp.computeArea(voisin);
        }
        // crée une nouvelle relation entre voisin et this
        NeighbohoodRelationship relation = new NeighbohoodRelationship();
        if (!this.order) {
            relation.addSymbolisedFeature(voisin);
            relation.addSymbolisedFeature(this.symbolisedFeature);
        }
        else {
            relation.addSymbolisedFeature(this.symbolisedFeature);
            relation.addSymbolisedFeature(voisin);
        }   
        relation.setType(type);
        if (this.symbolisedFeature.getArea() != 0 && voisin.getArea() != 0) {
            relation.setRatioAreas(Math.max(
                this.symbolisedFeature.getArea()/voisin.getArea(),
                voisin.getArea()/this.symbolisedFeature.getArea()));
        }
        else relation.setRatioAreas(1);
    }
  }
  
  public static void buildNR(SymbolisedFeature symbolisedFeature,
      FT_FeatureCollection<SymbolisedFeature> symboFC, double radius,
      int type, boolean order) {
    
    NRBuilder builder = new NRBuilder(symbolisedFeature, symboFC, radius, type, order);
    builder.buildNR();
  }
  
}
