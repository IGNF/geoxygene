package fr.ign.cogit.geoxygene.semio.legend.mapContent;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.feature.FT_Feature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.style.Layer;
import fr.ign.cogit.geoxygene.style.Rule;
import fr.ign.cogit.geoxygene.style.Style;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.semio.legend.legendContent.Legend;
import fr.ign.cogit.geoxygene.semio.legend.legendContent.LegendComponent;
import fr.ign.cogit.geoxygene.semio.legend.mapContent.operation.ContrastOp;
import fr.ign.cogit.geoxygene.semio.legend.mapContent.operation.NRBuilder;
import fr.ign.cogit.geoxygene.semio.legend.metadata.SemanticRelation;
import fr.ign.cogit.geoxygene.semio.legend.symbol.GraphicSymbol;
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
 * @author Elodie Buard - IGN / Laboratoire COGIT.
 * @author Sébastien Mustière - IGN / Laboratoire COGIT.
 * @author Charlotte Hoarau - IGN / Laboratoire COGIT.
 * 
 * One map, containing a set of symbolized objects.   
 * <p>
 * <strong>French:</strong><br />
 * Une carte, contenant un ensemble de familles d'objets cartographiques, 
 * symbolisés selon une légende définie avec les classes du package <code>legendContent</code> 
 * 
 */
public class Map {
    static Logger logger = Logger.getLogger(Map.class.getName());
    
    /**
     * name of the <code>Map</code>
     */
	private String name;
	
	/**
	 * @return the map name.
	 */
	public String getName() {
	  return name;
	}
	
	/**
	 * @param name the map name.
	 */
	public void setName(String name) {
	  this.name = name;
	}
	
	/**
	 * Mean of contrasts between close objects of the map 
	 */
	private Contrast meanContrast = new Contrast();
	/**
	 * Returns the mean of contrasts between close objects of the map.
	 */  
	public Contrast getMeanContrast() {
	  return meanContrast;
	}
	
	/**
	 * Specifies the mean of contrasts between close objects of the map.
	 */  
	public void setMeanContrast(Contrast constraste) {
	  this.meanContrast = constraste;
	}

	/**
	 * Map legend
	 */
	private Legend mapLegend;
	/**
	 * returns the map legend
	 */
	public Legend getMapLegend() {
	  return this.mapLegend;
	}
	
	/**
	 * Specifies the map legend
	 */
	public void setMapLegend(Legend O) {
		Legend old = this.mapLegend;
		this.mapLegend = O;
		if ( old != null ) {
		  old.getMaps().remove(this);
		}
		if ( O != null) {
			if ( ! O.getMaps().contains(this) ) {
			  O.getMaps().add(this);
			}
		}
	}
	

  /**
	 * Symbolized feature collections of the map. 
	 */
	private List<SymbolisedFeatureCollection> symbolisedFeatureCollections = 
	  new ArrayList<SymbolisedFeatureCollection>();
	
	/**
	 * Returns the symbolized feature collections of the map.
	 */
	public List<SymbolisedFeatureCollection> getSymbolisedFeatureCollections() {
	  return this.symbolisedFeatureCollections ;
	}
	
	/**
	 * Specifies the symbolized feature collections of the map.
	 */
	public void setSymbolisedFeatureCollections (List<SymbolisedFeatureCollection> L) {
		List<SymbolisedFeatureCollection> old = 
		  new ArrayList<SymbolisedFeatureCollection>(this.symbolisedFeatureCollections);
		Iterator<SymbolisedFeatureCollection> it1 = old.iterator();
		while ( it1.hasNext() ) {
			SymbolisedFeatureCollection O = it1.next();
			O.setMap(null);
		}
		Iterator<SymbolisedFeatureCollection> it2 = L.iterator();
		while ( it2.hasNext() ) {
			SymbolisedFeatureCollection O = it2.next();
			O.setMap(this);
		}
	}
	
	/**
	 * Add one symbolized feature collection to the map.
	 */
	public void addSymbolisedFeatureCollection (SymbolisedFeatureCollection O) {
		if ( O == null ) return;
		this.symbolisedFeatureCollections.add(O);
		O.setMap(this) ;
	}
	
	
	//////////////////////////////////////////////////////////
	///////////// METHODS 
	//////////////////////////////////////////////////////////

	/**
	 * Computes of all contrasts of the map. 
	 * Computes contrasts between all neighbors, 
	 * and then makes the averages at the features, feature collections and map levels.
	 * Mean contrasts are then stored on the corresponding attributes at those levels.
	 */ 
	public void instantiateAllContrasts() {
		// On efface tous les contrastes calculés
	    // (utile pour éviter de refaire 2 fois les calculs aprés)
		for (SymbolisedFeatureCollection famille : this.getSymbolisedFeatureCollections()) {
	        for (SymbolisedFeature objet : famille) {
				for (NeighbohoodRelationship relation : objet.getNeighborhoodRelationships()) {
					relation.setContrast(null);
				}
			}
		}
		// on recalcule les contrastes au niveau des objets puis des familles
		for (SymbolisedFeatureCollection famille : this.getSymbolisedFeatureCollections()) {
			for (SymbolisedFeature objet : famille) {
				for (NeighbohoodRelationship relation : objet.getNeighborhoodRelationships()) {
					// test de nullité d'abord, pour éviter de calculer le contraste deux fois,
					// une fois en parcourant l'objet1 et une autre fois l'objet2
					if (relation.getContrast() == null) {
						// calcule du contraste sur chaque relation
						relation.computeContrast();
					}
				}
				// calcule de la moyenne des contrastes au niveau des objets
				ContrastOp.computeMeanContrast(objet);
			}
			// calcule de la moyenne des contrastes au niveau des familles
			famille.computesMeanContrast();
            logger.info("contraste moyen famille " + famille.getName() + " : " + famille.getMeanContrast().getContrasteClarte());

		}
		
		// calcule de la moyenne des contrastes au niveau de la carte complète
		this.computeMeanContrast();
	}
	
	/**
     * Computes of all contrasts of the map. 
     * Computes contrasts between all neighbors, 
     * and then makes the averages at the features, feature collections and map levels.
     * Mean contrasts are then stored on the corresponding attributes at those levels.
     */ 
    public void instantiateAllWeightedContrasts() {
        // On efface tous les contrastes calculés
        // (utile pour éviter de refaire 2 fois les calculs aprés)
        for (SymbolisedFeatureCollection famille : this.getSymbolisedFeatureCollections()) {
            for (SymbolisedFeature objet : famille) {
                for (NeighbohoodRelationship relation : objet.getNeighborhoodRelationships()) {
                    relation.setContrast(null);
                }
            }
        }
        // on recalcule les contrastes au niveau des objets puis des familles
        for (SymbolisedFeatureCollection famille : this.getSymbolisedFeatureCollections()) {
            for (SymbolisedFeature objet : famille) {
                for (NeighbohoodRelationship relation : objet.getNeighborhoodRelationships()) {
                    // test de nullité d'abord, pour éviter de calculer le contraste deux fois,
                    // une fois en parcourant l'objet1 et une autre fois l'objet2
                    if (relation.getContrast() == null) {
                        // calcule du contraste sur chaque relation
                        relation.computeContrast();
                    }
                }
                // calcule de la moyenne des contrastes au niveau des objets
                ContrastOp.computeMeanContrast(objet);
            }
            // calcule de la moyenne des contrastes au niveau des familles
            famille.computesWeightedMeanContrast();
            logger.info("contraste moyen pondéré famille " + famille.getName() + " : " + famille.getMeanContrast().getContrasteClarte());
            
        }
        
        // calcule de la moyenne des contrastes au niveau de la carte complète
        this.computeMeanContrast();
    }

		
	/**
	 * Computes the mean contrasts of the <code>SymbolizedFeatureCollectioin</code>s
	 *  of the <code>Map</code>
	 * <strong>French: </strong>Calcule la moyenne des contrastes moyens des familles
	 *  de la carte.
	 */
	private void computeMeanContrast() {
		if (this.getMeanContrast()==null) {
			this.setMeanContrast(new Contrast());
		}
		
		double sommeTeinte = 0;
		double sommeClarte = 0;
		double sommeQualiteTeinte = 0;
		double sommeQualiteClarte = 0;
		int nbfamilles=0; 
		List<SymbolisedFeatureCollection> famillescartos = this.getSymbolisedFeatureCollections();
		for (SymbolisedFeatureCollection familleCarto : famillescartos) {
			// On ne prend pas en compte les familles au contraste non défini (i.e. sans relation)
			if (familleCarto.getMeanContrast().getContrasteTeinte()==-1) continue;
			nbfamilles++;
			sommeTeinte = sommeTeinte + familleCarto.getMeanContrast().getContrasteTeinte();
			sommeClarte = sommeClarte + familleCarto.getMeanContrast().getContrasteClarte();
			sommeQualiteTeinte = sommeQualiteTeinte + familleCarto.getMeanContrast().getQualiteContrasteTeinte();
			sommeQualiteClarte = sommeQualiteClarte + familleCarto.getMeanContrast().getQualiteContrasteClarte();
		}
		if (nbfamilles != 0) {
			this.getMeanContrast().setContrasteTeinte(sommeTeinte/nbfamilles);
			this.getMeanContrast().setContrasteClarte(sommeClarte/nbfamilles);
			this.getMeanContrast().setQualiteContrasteTeinte(sommeQualiteTeinte/nbfamilles);
			this.getMeanContrast().setQualiteContrasteClarte(sommeQualiteClarte/nbfamilles);
		}
		else {
			logger.info("WARNING: Il n'y a en fait aucune relation de voisinage");
			this.getMeanContrast().setContrasteTeinte(-1);
			this.getMeanContrast().setContrasteClarte(-1);
			this.getMeanContrast().setQualiteContrasteTeinte(-1);
			this.getMeanContrast().setQualiteContrasteClarte(-1);
		}
	}
	
	/**
	 * Determines which features are neighbors on the map.
	 * <p>
	 * <strong>NotaBene: </strong>
	 * - do not consider features of the same collection <br />
	 * - do only consider features belonging to related collections <br />
	 * (ie. participating in a asociation/dissociation/order relationship) <br />
	 * - For each couple of features close enough, only one neighborhood relationship is created. 
	 * <p>
	 * <strong>French: </strong>Methode qui détermine quels objets sont en relation 
	 * de voisinage sur la carte.
	 * <p>
	 * <strong>NotaBene: </strong>
	 * - On ne cherche pas de relations entre objets de la meme famille <br />
	 * - On ne cherche des relations qu'entre objets de familles en relation <br />
	 * - On n'instancie qu'une relation pour chaque couple d'objet 
	 * (i.e. pas (obj1,obj2) et (obj2,obj1))
	 *  
	 * @param maxDistance Objects are said neighbors only if ditance between them is less than this threshold. 
	 */
	public void searchForNeighbors(double maxDistance) {
	  logger.info("Recherche des relations de voisinages.");
		for(int i=0; i<this.getSymbolisedFeatureCollections().size(); i++) {
			SymbolisedFeatureCollection famille1 = this.getSymbolisedFeatureCollections().get(i);
			for(int j=i+1; j<this.getSymbolisedFeatureCollections().size(); j++) {
				SymbolisedFeatureCollection famille2 = this.getSymbolisedFeatureCollections().get(j);
				// on recherche si les deux familles sont en relation
				List<SemanticRelation> relations = famille1.getLegend().getRelations();
				for (SemanticRelation relation : relations) {
					if ( relation.getRelatedComponents().contains(famille2.getLegend()) ) {
						// NB: si deux familes sont en relations de deux maniéres,
						// on crée deux relations de voisinage pour chaque couple
						// d'objets voisins
						boolean ordre = true;
						if (relation.getType() == SemanticRelation.ORDER) {
							// determination de la famille la plus "faible" dans une relation d'ordre
							// on parcours les éléments de la relation dans l'ordre et on cherche le
							// premier rencontré
							for (LegendComponent element : relation.getRelatedComponents()) {
								if (element == famille1.getLegend()) break;
								if (element == famille2.getLegend()) {
									ordre = false;
									break;
								}
							}
						}
						for (SymbolisedFeature objetCarto : famille1) {
							// Détermine les voisins d'un objet en cours de traitement
							// La notion de voisinage est déterminée sur des critères topologiques et géométriques
							NRBuilder.buildNR(objetCarto, famille2, maxDistance, relation.getType(), ordre);
						}
					}
				}
			}
			int nbVoisins = 0;
			for (SymbolisedFeature objet : famille1) {
				nbVoisins = nbVoisins+objet.getNeighborhoodRelationships().size();
			}
			logger.info(nbVoisins + " relations de voisinage avec la famille " + famille1.getName());
		}
		
	}

	public List<Float> getAreas(){
		List<Float> areas = new ArrayList<Float>();
		for (SymbolisedFeatureCollection collection : this.getSymbolisedFeatureCollections()) {
			if (collection.getTotalArea() == -1) {
				collection.computesTotalArea();
			}
			Double area = collection.getTotalArea();
			areas.add(area.floatValue());
		}
		return areas;
	}
	
	
	///////////////////////////////////////////////////////////////////
	/////////////    Constructors
	///////////////////////////////////////////////////////////////////
	public Map(){}

	@SuppressWarnings("unchecked")
	public Map(List<Layer> layers, Legend legend, double resolution){
		List<SymbolisedFeatureCollection> collections = new ArrayList<SymbolisedFeatureCollection>();
		
		for (Layer layer : layers) {
			for (Style style : layer.getStyles()) {
				for (Rule rule : style.getFeatureTypeStyles().get(0).getRules()) {

					SymbolisedFeatureCollection collection = new SymbolisedFeatureCollection();
					collection.setName(rule.getName());
					collection.setLegend(legend.getLeaf(layer.getName()));
					collection.getLegend().setSymbol(new GraphicSymbol(layer, resolution));
					// logger.info(collection.getName() + " : " + collection.getColor());
					
					// Getting the features of the symbolisedFeatureCollection
					IFeatureCollection<IFeature> layerFeatureCollection = (IFeatureCollection<IFeature>) layer
							.getFeatureCollection();
					collection.setFeatureType(layerFeatureCollection
							.getFeatureType());
					for (IFeature layerFeature : layerFeatureCollection) {
						SymbolisedFeatureDecorator feature = new SymbolisedFeatureDecorator(
								layerFeature);
						feature.setSymbolisedFeatureCollection(collection);
					}
					collections.add(collection);
				}
			}
		}
		this.setSymbolisedFeatureCollections(collections);
		this.setMapLegend(legend);
	}
	
	@SuppressWarnings("unchecked")
    public Map(List<Layer> layers, Legend legend){
        List<SymbolisedFeatureCollection> collections = new ArrayList<SymbolisedFeatureCollection>();
        
        for (Layer layer : layers) {
            logger.info("Nom du layer = " + layer.getName());
            for (Style style : layer.getStyles()) {
                
                logger.info("Style name = " + style.getName() + " : " + style.getFeatureTypeStyles().size());
                
                for (Rule rule : style.getFeatureTypeStyles().get(0).getRules()) {

                    logger.info("Rule name = " + rule.getName());
                    SymbolisedFeatureCollection collection = new SymbolisedFeatureCollection();
                    collection.setName(rule.getName());
                    collection.setLegend(legend.getLeaf(layer.getName()));
                    collection.getLegend().setSymbol(new GraphicSymbol(layer, 5));
                    logger.info(collection.getName() + " : " + collection.getColor());
                    
                    // Getting the features of the symbolisedFeatureCollection
                    FT_FeatureCollection<FT_Feature> layerFeatureCollection = (FT_FeatureCollection<FT_Feature>) layer.getFeatureCollection();
                    collection.setFeatureType(layerFeatureCollection.getFeatureType());
                    /*for (FT_Feature layerFeature : layerFeatureCollection) {
                         SymbolisedFeatureDecorator feature = new SymbolisedFeatureDecorator(layerFeature);
                         feature.setSymbolisedFeatureCollection(collection);
                    }*/
                    collections.add(collection);
                }
            }
        }
        this.setSymbolisedFeatureCollections(collections);
        this.setMapLegend(legend);
    }

}
