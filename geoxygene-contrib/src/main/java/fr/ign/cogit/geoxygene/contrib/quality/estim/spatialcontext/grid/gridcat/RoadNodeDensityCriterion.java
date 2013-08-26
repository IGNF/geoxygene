package fr.ign.cogit.geoxygene.contrib.quality.estim.spatialcontext.grid.gridcat;

import java.util.Collection;

import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;

/**
 * 
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * 
 * @copyright IGN
 * 
 * @author JFGirres
 */
public class RoadNodeDensityCriterion extends CellCriterion {


	public RoadNodeDensityCriterion(GridCell cell, double poids,
			Number seuilBas, Number seuilHaut) {
		super(cell, poids, seuilBas, seuilHaut);
	}

	@Override
	public void setCategory() {
		// cas d'un crit�re entier
		int valeurInt = getValeur().intValue();
		if(valeurInt<getSeuilBas().intValue()){setClassif(1);}
		else if(valeurInt>getSeuilHaut().intValue()){setClassif(3);}
		else{setClassif(2);}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setValue() {
		// create un circle around this cell
		DirectPosition centre = new DirectPosition(getCellule().getxCentre(), 
				getCellule().getyCentre());
		GM_Polygon circle = GeometryFactory.buildCircle(centre, 
				getCellule().getGrille().getRadiusCellule(), 24);
		// get data
		FT_FeatureCollection<DefaultFeature> data = (FT_FeatureCollection<DefaultFeature>)
		 this.getCellule().getGrille().getData().get(UrbanGrid.FC_NODES);
		Collection<DefaultFeature> cellNodes = data.select(circle);
		this.setValeur(new Integer(cellNodes.size()));
	}

}
