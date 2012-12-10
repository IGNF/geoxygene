package fr.ign.cogit.geoxygene.semio.legend.legendContent;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import fr.ign.cogit.geoxygene.semio.legend.mapContent.SymbolisedFeatureCollection;
import fr.ign.cogit.geoxygene.semio.legend.symbol.GraphicSymbol;

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
 * One line of the key (ie. symbol choices applied to a collection of features).  
 * <p>
 * <strong>French:</strong><br />
 * Une <code>LegendLeaf</code> est une ligne de légende, qui définit la symbolisation
 * à appliquer aux objets d'une ou plusieurs familles carto. 
 * <p>
 *
 * @see Legend
 * @see LegendComponent
 * @see LegendComposite
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LegendLeaf",
		propOrder = {
			"symbol",
			"symbolisedFeatureCollections"
		})
public class LegendLeaf extends LegendComponent {
	/**
	 * {@link GraphicSymbol} symbolizing this LegendLeaf.
	 */
	@XmlElement(name = "symbol")
	private GraphicSymbol symbol;
	
	/**
	 * Returns the {@link GraphicSymbol} of the LegendLeaf.
	 * @return The{@link GraphicSymbol} of the LegendLeaf.
	 */ 
	public GraphicSymbol getSymbol() {
	  return symbol;
	}
	
	/**
	 * Sets the {@link GraphicSymbol} of the LegendLeaf.
	 * @param symbol The {@link GraphicSymbol} of the LegendLeaf.
	 */ 
	public void setSymbol(GraphicSymbol symbol) {
	  this.symbol = symbol;
	}

	/**
	 * Feature collections symbolized by this.
	 */
	@XmlElement(name = "symbolisedFeatureCollections")
	private List<SymbolisedFeatureCollection> symbolisedFeatureCollections = 
	  new ArrayList<SymbolisedFeatureCollection>();
	
	/**
	 * Returns the Feature collections symbolized by this.
	 * @return The Feature collections symbolized by this.
	 */
	public List<SymbolisedFeatureCollection> getSymbolisedFeatureCollections() {
	  return this.symbolisedFeatureCollections;
	}
	
	/**
	 * Sets the Feature collections symbolized by this.
	 * @param collections Feature collections symbolized by this.
	 */
	public void setSymbolisedFeatureCollections (
	            List<SymbolisedFeatureCollection> collections) {
		List<SymbolisedFeatureCollection> old = 
		  new ArrayList<SymbolisedFeatureCollection>(this.symbolisedFeatureCollections);
		for (SymbolisedFeatureCollection O : old) {
		  O.setLegend(null);
		}
		for (SymbolisedFeatureCollection O : collections) {
		  O.setLegend(this);
		}
	}
	
	/**
	 * Add a Feature collection to the Feature collections symbolized by this.
	 * @param collection Feature collection symbolized by this.
	 */
	public void addSymbolisedFeatureCollection (SymbolisedFeatureCollection collection) {
		if ( collection == null ) {
		  return;
		}
		this.symbolisedFeatureCollections.add(collection);
		collection.setLegend(this) ;
	}
	
}