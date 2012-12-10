package fr.ign.cogit.geoxygene.semio.legend.mapContent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import fr.ign.cogit.geoxygene.style.colorimetry.ColorimetricColor;

import fr.ign.cogit.geoxygene.semio.legend.metadata.SemanticRelation;
import fr.ign.cogit.geoxygene.semio.legend.symbol.color.Contrast;
import fr.ign.cogit.geoxygene.semio.legend.symbol.color.ContrastCollection;

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
 * 
 * Relationship between two features neighbors on the map.
 *  
 */
public class NeighbohoodRelationship {
	/**
	 * Contrast between the two neighbor features.
	 */
	private Contrast contrast = new Contrast();
	
	/**
	 * @return Contrast between the two neighbor features.
	 */
	public Contrast getContrast() {
	  return contrast;
	}
	
	/**
	 * @param contrast Contrast between the two neighbor features.
	 */
	public void setContrast(Contrast contrast) {
	  this.contrast = contrast;
	}

	/**
	 * Type of relation between the two neighbor features.
	 * <strong>NB: </strong>redundant information duplicated for the sake of simplicity.
	 * Possible values: see static variables on {@link SemanticRelation}
	 */
	private int type = SemanticRelation.UNKNOWN;
	
	/**
	 * Returns the type of relation between the two neighbor features.
	 * <strong>NB: </strong>redundant information duplicated for the sake of simplicity.
	 * Possible values: see static variables on {@link SemanticRelation}
	 */
	public int getType() {
	  return type;
	}
	
	/**
	 * Specifies the type of relation between the two neighbor features.
	 * <strong>NB: </strong>redundant information duplicated for the sake of simplicity.
	 * Possible values: see static variables on {@link SemanticRelation}
	 */
	public void setType(int type) {this.type = type;}

	
	
	/**
	 * Ratio of surfaces between neighbor features.
	 * always >1 : size of the biggest feature / size of the smallest one.
	 */
	protected double ratioAreas;
	
	/**
	 * Returns the ratio of surfaces between neighbor features.
	 * always >1 : size of the biggest feature / size of the smallest one.
	 */
	public double getRatioAreas() {
	  return this.ratioAreas;
	}
	
	/**
	 * Specifies the ratio of surfaces between neighbor features.
	 * always >1 : size of the biggest feature / size of the smallest one.
	 */
	public void setRatioAreas(double rapport) {
		this.ratioAreas = rapport;
	}
	
	/**
	 * Features (2) concerned by the relationship
	 * <strong>NB: </strong>in case of relationship types as "order",
	 * the first feature is the less important one.  
	 */
	public List<SymbolisedFeature> symbolisedFeatures = new ArrayList<SymbolisedFeature>();
	
	/**
	 * Returns the (2) features concerned by the relationship
	 * <strong>NB: </strong>in case of relationship types as "order", 
	 * the first feature is the less important one.
	 */  
	public List<SymbolisedFeature> getSymbolisedFeatures() {
	  return this.symbolisedFeatures ;
	}
	
	/**
	 * Specifies the (2) features concerned by the relationship
	 * <strong>NB: </strong>in case of relationship types as "order", 
	 * the first feature is the less important one.
	 */  
	public void setSymbolisedFeatures (List<SymbolisedFeature> L) {
		List<SymbolisedFeature> old = 
		  new ArrayList<SymbolisedFeature>(this.symbolisedFeatures);
		Iterator<SymbolisedFeature> it1 = old.iterator();
		while ( it1.hasNext() ) {
			SymbolisedFeature O = it1.next();
			this.symbolisedFeatures.remove(O);
			O.getNeighborhoodRelationships().remove(this);
		}
		Iterator<SymbolisedFeature> it2 = L.iterator();
		while ( it2.hasNext() ) {
			SymbolisedFeature O = it2.next();
			this.symbolisedFeatures.add(O);
			O.getNeighborhoodRelationships().add(this);
		}
	}
	
	/**
	 * Adds one feature concerned by the relationship
	 * <strong>NB: </strong>in case of relationship types as "order", 
	 * the first feature is the less important one.
	 */  
	public void addSymbolisedFeature (SymbolisedFeature O) {
		if ( O == null )  {
		  return;
		}
		this.symbolisedFeatures.add(O) ;
		O.getNeighborhoodRelationships().add(this);
	}
	
	/**
	 * Removes one feature concerned by the relationship
	 * NB: in case of relationship types as "order",
	 * the first feature is the less important one.
	 */  
	public void removeSymbolisedFeature (SymbolisedFeature O) {
		if ( O == null ) {
		  return;
		}
		this.symbolisedFeatures.remove(O) ; 
		O.getNeighborhoodRelationships().remove(this);
	}
	
	//////////////////////////////////////
	// METHODS
	//////////////////////////////////////

	/**
	 * TODO transférer cette méthode vers la classe Contrast
	 * Computes the quality of contrast between two neighbors
	 * Marks are between 0 and 10: 0 = good quality, 10 = bad quality 
	 * 
	 * <strong>French:</strong><br />
	 * Calcul de la qualité des contrastes 
	 * (comparaison entre valeurs de contrastes brutes et valeurs acceptables).
	 * 
	 * Les notes de qualité de contrastes sont entre 0 et 10.
	 * <strong>Warning:</strong> 0 = bonne note, 10 = mauvaise note.
	 * 
	 * Note = 0 si la valeur est dans l'intervalle acceptable
	 * Note = écart à borne inf acceptable si la valeur est inférieure à cette borne
	 * Note = écart à borne sup acceptable si la valeur est supérieure à cette borne
	 */
	private double computeContrastQuality() {
		// Qualité du contraste de clarté. Il dépend de:
		// - la valeur du contraste, 
		// - du type de relation, 
		// - et du rapport de surfaces entre 
		double valeurQualiteClarte = 0;
		
		// Récupération des bornes de contraste acceptables (entre 0 et 10)
		List<Integer> conditionClarte = Contrast.getLightnessContrastQualityBounds(this.getType(), this.getRatioAreas());
		double borneInfClarte = conditionClarte.get(0).doubleValue();
		double borneSupClarte = conditionClarte.get(conditionClarte.size()-1).doubleValue();
		
		// Comparaison entre la valeur brute et les valeurs acceptables
		if (this.getContrast().getContrasteClarte() > borneInfClarte
		    && this.getContrast().getContrasteClarte()<borneSupClarte) {
			valeurQualiteClarte = 0;
		} else if (this.getContrast().getContrasteClarte() < borneInfClarte) {
			valeurQualiteClarte = borneInfClarte - this.getContrast().getContrasteClarte();
		} else if (this.getContrast().getContrasteClarte() > borneSupClarte) {
			valeurQualiteClarte = this.getContrast().getContrasteClarte()-borneSupClarte;
		}
		this.getContrast().setQualiteContrasteClarte(valeurQualiteClarte);

		// Qualité du contraste de teinte. Il dépend de:
		// - la valeur du contraste, 
		// - du type de relation (à travers les valeurs idéales dans "conditionNoteTeinte")
		double valeurQualiteTeinte= 0;
		
		// Récupération des bornes de contraste acceptables (entre 0 et 10)
		List<Integer> conditionTeinte = Contrast.getHueContrastQualityBounds(this.getType());
		double borneInfTeinte = conditionTeinte.get(0).doubleValue();
		double borneSupTeinte = conditionTeinte.get(conditionTeinte.size() - 1).doubleValue();
		
		// Comparaison entre la valeur brute et les valeurs acceptables
		if (this.getContrast().getContrasteTeinte() > borneInfTeinte
		    && this.getContrast().getContrasteTeinte()< borneSupTeinte) {
			valeurQualiteTeinte = 0;
		} else if (this.getContrast().getContrasteTeinte() < borneInfTeinte) {
			valeurQualiteTeinte = borneInfTeinte - this.getContrast().getContrasteTeinte();
		} else if (this.getContrast().getContrasteTeinte() > borneSupTeinte) {
			valeurQualiteTeinte = this.getContrast().getContrasteTeinte() - borneSupTeinte;
		}
		this.getContrast().setQualiteContrasteTeinte(valeurQualiteTeinte);

		return valeurQualiteClarte;
	}
	
	/**
	 * Computes the contrast between the two neighors 
	 * (raw values of contrast AND marks for the quality of contrasts) 
	 * 
	 * <strong>French:</strong><br />
	 * Calcule le contraste entre les objets en relation 
	 * (valeurs brutes de contrastes ET évaluation de la qualité des contrastes).
	 */
	public void computeContrast() {
		// contraste brut
		ColorimetricColor c1 = this.getSymbolisedFeatures().get(0)
		                          .getSymbolisedFeatureCollection().getColor();
		ColorimetricColor c2 = this.getSymbolisedFeatures().get(1)
		                          .getSymbolisedFeatureCollection().getColor();
		
		// NB: ici partie dépendant du cercle chromatique
		Contrast contrast = ContrastCollection.getCOGITContrast(c1, c2);
		this.setContrast(contrast);
		
		// qualité du contraste
		this.computeContrastQuality();				
	}

}