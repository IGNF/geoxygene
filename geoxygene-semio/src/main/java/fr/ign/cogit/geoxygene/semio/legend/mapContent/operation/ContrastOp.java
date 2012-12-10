package fr.ign.cogit.geoxygene.semio.legend.mapContent.operation;

import fr.ign.cogit.geoxygene.semio.legend.mapContent.NeighbohoodRelationship;
import fr.ign.cogit.geoxygene.semio.legend.mapContent.SymbolisedFeature;
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
 * @author Elodie Buard - IGN / Laboratoire COGIT
 * @author Sébastien Mustière - IGN / Laboratoire COGIT
 * @author Charlotte Hoarau - IGN / Laboratoire COGIT
 *
 */
public class ContrastOp {
  
  private SymbolisedFeature symbolisedFeature;
  
  public SymbolisedFeature getSymbolisedFeature() {
    return symbolisedFeature;
  }
  
  public void setSymbolisedFeature(SymbolisedFeature symbolisedFeature) {
    this.symbolisedFeature = symbolisedFeature;
  }
  
  public ContrastOp(){};
  
  public ContrastOp(SymbolisedFeature symbolisedFeature) {
    super();
    this.symbolisedFeature = symbolisedFeature;
  }
  
  /**
   * Calcule la moyenne des contrastes entre cet élément 
   * et tous ses voisins sur la carte (moyenne des valeurs burtes de 
   * contraste et des notes de qualité de contraste).
   * 
   * NB: si l'objet n'a pas de voisin,
   * le contraste associé a les valeurs par défaut
   * (-1 à toutes ses notes)
   */
  public void computeMeanContrast() {
    // Cas où il n'y a pas de voisin
    if (this.symbolisedFeature.getNeighborhoodRelationships().size() == 0) { 
      this.symbolisedFeature.setContrast(new Contrast());
        return;
    }
    // Cas où il y a des voisins
    if (this.symbolisedFeature.getContrast()==null) {
      this.symbolisedFeature.setContrast(new Contrast());
    }
    double sommeTeinte = 0;
    double sommeClarte = 0;
    double sommeQualiteTeinte = 0;
    double sommeQualiteClarte = 0;
    for (NeighbohoodRelationship relation : this.symbolisedFeature.getNeighborhoodRelationships()) {
        sommeTeinte = sommeTeinte + relation.getContrast().getContrasteTeinte();
        sommeClarte = sommeClarte + relation.getContrast().getContrasteClarte();
        sommeQualiteTeinte = 
            sommeQualiteTeinte + relation.getContrast().getQualiteContrasteTeinte();
        sommeQualiteClarte = 
            sommeQualiteClarte + relation.getContrast().getQualiteContrasteClarte();
    }
    this.symbolisedFeature.getContrast().setContrasteTeinte(
        sommeTeinte / this.symbolisedFeature.getNeighborhoodRelationships().size());
    this.symbolisedFeature.getContrast().setContrasteClarte(
        sommeClarte / this.symbolisedFeature.getNeighborhoodRelationships().size());
    this.symbolisedFeature.getContrast().setQualiteContrasteTeinte(
        sommeQualiteTeinte / this.symbolisedFeature.getNeighborhoodRelationships().size());
    this.symbolisedFeature.getContrast().setQualiteContrasteClarte(
        sommeQualiteClarte / this.symbolisedFeature.getNeighborhoodRelationships().size());
  }
  
  public static void computeMeanContrast(SymbolisedFeature symbolisedFeature){
    ContrastOp contrastOp = new ContrastOp(symbolisedFeature);
    contrastOp.computeMeanContrast();
  }
}