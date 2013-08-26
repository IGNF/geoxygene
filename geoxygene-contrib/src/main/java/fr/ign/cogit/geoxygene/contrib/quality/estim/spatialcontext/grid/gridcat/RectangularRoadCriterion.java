package fr.ign.cogit.geoxygene.contrib.quality.estim.spatialcontext.grid.gridcat;

import java.util.Collection;
import java.util.Iterator;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
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
public class RectangularRoadCriterion extends CellCriterion {

	
	public RectangularRoadCriterion(GridCell cell, double poids, Number seuilBas,
			Number seuilHaut) {
		super(cell, poids, seuilBas, seuilHaut);
	}

	@Override
	/**
	 * Calcule le crit�re de rectangularit� dans la cellule. Il s'agit d'une 
	 * moyenne des angles incidents des noeuds routiers de degr� > 2, ramen�s
	 * entre 0 et Pi/2. Plus cette valeur est proche de Pi/2, plus la
	 * rectangularit� est importante.
	 * 
	 */
	public void setCategory() {
		// cas d'un crit�re r�el
		double valeurDbl = getValeur().doubleValue();
		if(valeurDbl<getSeuilBas().doubleValue()){setClassif(1);}
		else if(valeurDbl>getSeuilHaut().doubleValue()){setClassif(3);}
		else{setClassif(2);}
	}

	/**
	 * Version non optimis�e: le lien noeud/route se fait par la g�om�trie
	 * {@inheritDoc} (This is the behaviour inherited from the super class). <p>
	 * 
	 * @author GTouya
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void setValue() {
		double totalRect = 0.0;
		int nbNoeuds = 0;
		// create un circle around this cell
		DirectPosition centre = new DirectPosition(getCellule().getxCentre(), 
				getCellule().getyCentre());
		GM_Polygon circle = GeometryFactory.buildCircle(centre, 
				getCellule().getGrille().getRadiusCellule(), 24);
		FT_FeatureCollection<DefaultFeature> nodes = (FT_FeatureCollection<DefaultFeature>)
		 this.getCellule().getGrille().getData().get(UrbanGrid.FC_NODES);
		FT_FeatureCollection<DefaultFeature> roads = (FT_FeatureCollection<DefaultFeature>)
		 this.getCellule().getGrille().getData().get(UrbanGrid.FC_ROADS);
		Collection<DefaultFeature> cellNodes = nodes.select(circle);
		// on parcourt cette collection
		for(IFeature node:cellNodes){
			Collection<DefaultFeature> nodeRoads = roads.select(node.getGeom());
			// on filtre selon son degr�
			if(nodeRoads.size()<3) continue;
			double totalAnglesNoeud = 0.0;
			int nbAnglesNoeud = 0;
			// on fait la moyenne des angles ramen�s entre 0 et 90
			// entre les tron�ons cons�cutifs
			Iterator<DefaultFeature> iter = nodeRoads.iterator();
			IFeature first = iter.next();
			IFeature road1 = first;
			IFeature road2 = iter.next();
			for(int i=0;i<nodeRoads.size()-1;i++){
				double angle = GeomAlgorithms.angleBetween2Lines(
						(GM_LineString)road1.getGeom(), (GM_LineString)road2.getGeom());
				// on ram�ne dans [0,Pi]
				if(angle<0.0){angle = -angle;}
				// on le ram�ne dans [0,Pi/2]
				if(angle>Math.PI/2.0){angle = Math.PI - angle;}
				// on incr�mente le nb d'angles
				nbAnglesNoeud += 1;
				totalAnglesNoeud += angle;
				// on passe � l'angle suivant
				road1 = road2;
				if(iter.hasNext()){road2 = iter.next();}
				else{road2 = first;}
			}
			// on calcule la rectangularit� du noeud
			double rectangularite = totalAnglesNoeud / nbAnglesNoeud;
			
			// on met � jour les infos sur la cellule
			nbNoeuds += 1;
			totalRect += rectangularite;
			
		}// while boucle sur setNoeuds
		
		// on calcule la moyenne sur la cellule
		double rectCell = totalRect / nbNoeuds;
		setValeur(new Double(rectCell));
	}

}
