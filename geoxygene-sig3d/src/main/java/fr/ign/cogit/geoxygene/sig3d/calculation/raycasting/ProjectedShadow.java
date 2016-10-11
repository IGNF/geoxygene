package fr.ign.cogit.geoxygene.sig3d.calculation.raycasting;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IRing;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.geometrie.Vecteur;
import fr.ign.cogit.geoxygene.convert.FromGeomToSurface;
import fr.ign.cogit.geoxygene.sig3d.equation.ApproximatedPlanEquation;
import fr.ign.cogit.geoxygene.sig3d.equation.LineEquation;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Ring;
import fr.ign.cogit.geoxygene.util.index.Tiling;


/**
 * 
 *   
 *        This software is released under the licence CeCILL
 * 
 *        see LICENSE.TXT
 * 
 *        see <http://www.cecill.info/ http://www.cecill.info/
 * 
 * 
 * 
 * @copyright IGN
 * 
 * @author Brasebin Mickaël
 * 
 * @version 1.7
 * 
 *
 * 
 * Classe permettant de générer la projection d'un objet sur une géométrie. Plusieurs résultats sont possibles : point, ligne, polygone ou solide
 *
 */
public class ProjectedShadow {

	public static enum POSSIBLE_RESULT {
		POINT_PROJECTED, LINE_TO_PROJECTED, PROJECTED_POLYGON, PROJECTED_VOLUME
	};

	/**
	 * 
	 * @param lBatiments
	 *            les bâtiments à partir desquels on peut projeter
	 * @param collectionSupport
	 *            les objets sur lesquels on projete les ombres (attention on
	 *            utilise l'index spatial dessus les objets verticaux seront
	 *            difficile à considérer pour le moment)
	 * @param emprise
	 *            si non null, cette géométrie se substitue à la collection
	 *            support si il n'y a pas d'intersection
	 * @param v
	 *            la direction dans laquelle on projette l'ombre
	 * @param frontClip
	 *            à partir de quelle fraction de f on commence à vérifier si il
	 *            y a possible projection de l'ombre
	 * @param backClip
	 *            distance jusqu'à laquelle on considère possible la projesction
	 *            de l'ombre
	 * @return
	 */
	public static List<IGeometry> process(
			IFeatureCollection<? extends IFeature> lBatiments,
			IFeatureCollection<? extends IFeature> collectionSupport,
			IPolygon emprise, Vecteur v, double frontClip, double backClip,
			POSSIBLE_RESULT PR, boolean fast) {

		// Vecteur vDebug = v.multConstante(50);

		List<IGeometry> lGeom = new ArrayList<IGeometry>();

		if (!collectionSupport.hasSpatialIndex()) {

			collectionSupport.initSpatialIndex(Tiling.class, false);
		}

		// int count = 0;

		// Pour chaque bâtiment on :
		// 1) Récupère les sommets
		// 2) on récupère les parcelles sur lesquelles les ombres peuvent être
		// projetée
		// 3) on rècupère leur géométrie
		// 4) on les ordonne
		// 5) On génère un rayon à partir du sommet du bâtiment et du vecteur
		// 6) On parcourt les géométries de la plus proche à la plus loin tant
		// qu'on n'a pas d'intersection on continue
		// 7) on a une intersection, on met le point dans la liste des points
		// projetés
		// 8) on n'a pas d'intersection on tente de projeter sur l'emprise, si
		// elle est définie
		// 9) On génère la géométrie ad hoc

		for (IFeature b : lBatiments) {

			// count++;
			/*
			 * 
			 * if(count != 2){ continue; }
			 */

			// 1) On récupère de manière unique les points du bâtiments

			IDirectPositionList dpl;
			IDirectPositionList dplProj = new DirectPositionList();
			
			
			if(fast){
				dpl = preprocessPoint(b, v);
			}else{
				dpl =  b.getGeom().coord();
				dpl = removeDouble(dpl);
			}

		

			// 2) On parcourt les points

			for (IDirectPosition dp : dpl) {

				// On génère une segment qui part du sommet + fronClip * vecteur

				Vecteur vFC = v.multConstante(frontClip);

				LineEquation le = new LineEquation(dp, v);

				IDirectPositionList dpl2 = new DirectPositionList();
				dpl2.add(vFC.translate(dp));

				// Qui arrive à + backclipClip * vecteur

				Vecteur vBC = v.multConstante(backClip);

				dpl2.add(vBC.translate(dp));

				ILineString ls = new GM_LineString(dpl2);

				// On sélectionne les parcelles concernées (optimisable à cet
				// endroit : on le fait pour chaque sommet)

				Collection<? extends IFeature> cP = collectionSupport
						.select(ls);

				if (cP == null) {
					dplProj.add(null);
					continue;
				}

				// On récupère toutes les géométries
				List<IGeometry> lGeomParcelle = new ArrayList<IGeometry>();
				for (IFeature sP : cP) {
					lGeomParcelle.add(sP.getGeom());
				}

				// On les ordonnes du plus proche au plus loin

				List<IGeometry> lPord = orderByDistance(lGeomParcelle,
						new GM_Point(dp));

				IDirectPosition dplast = null;

				// On regarde pour chaque géométrie si il y a une intersection
				bouclegeom: for (IGeometry pOrd : lPord) {

					// On décompose la géométrie en polygones, si besoin est.
					List<IOrientableSurface> lOS = FromGeomToSurface
							.convertGeom(pOrd);

					for (IOrientableSurface os : lOS) {
						// On effectue le calcul d'intersection pour chaque
						// polygone
						dplast = RayCasting
								.intersectionPolygonLine(le, (IPolygon) os);
						// On a quelque chose, on quitte la boucle sur les
						// géométries
						if (dplast != null) {
							break bouclegeom;
						}

					}

				}

				// Si on a un point c'est le bon
				// Solution intermédiaire : on projet sur l'emprise
				if (dplast == null) {
					if (emprise != null) {
						dplast = RayCasting
								.intersectionPolygonLine(le, emprise);
					}

				}

				// On ajoute le point qui peut être null si pas d'intersection
				dplProj.add(dplast);

				// lGeom.add(new GM_Point(dplast));
				/*
				 * if(dplast == null){ IDirectPositionList dplOut = new
				 * DirectPositionList(); dplOut.add(dp);
				 * 
				 * 
				 * 
				 * 
				 * dplOut.add(vDebug.translate(dp)); lGeom.add(new
				 * GM_LineString(dplOut));
				 * 
				 * continue; }
				 * 
				 * 
				 * IDirectPositionList dplOut = new DirectPositionList();
				 * dplOut.add(dplast); dplOut.add(dp); lGeom.add(new
				 * GM_LineString(dplOut));
				 */
			}

			// Etape 9 : on générer la géométrie ad hocs

			switch (PR) {
			case POINT_PROJECTED:
				lGeom.addAll(generatePoint(dplProj));
				break;
			case LINE_TO_PROJECTED:
				lGeom.addAll(generateLine(dpl, dplProj));
				break;
			case PROJECTED_POLYGON:
				lGeom.addAll(generatePolygones(dpl,dplProj,b));
				break;
			case PROJECTED_VOLUME:
				lGeom.addAll(generateVolume(dpl,dplProj,b,v));
				break;

			}

		}

		return lGeom;

	}


	private static List<IGeometry> generatePoint(IDirectPositionList dpl) {

		List<IGeometry> lGeom = new ArrayList<IGeometry>();

		for (IDirectPosition dp : dpl) {
			lGeom.add(new GM_Point(dp));
		}

		return lGeom;
	}

	private static List<IGeometry> generateLine(IDirectPositionList dpl,
			IDirectPositionList dplProj) {

		List<IGeometry> lGeom = new ArrayList<IGeometry>();

		int nbSom = dpl.size();

		for (int i = 0; i < nbSom; i++) {

			IDirectPosition dp1 = dpl.get(i);
			IDirectPosition dp2 = dplProj.get(i);

			if (dp2 != null) {
				IDirectPositionList dplTemp = new DirectPositionList();
				dplTemp.add(dp1);
				dplTemp.add(dp2);

				lGeom.add(new GM_LineString(dplTemp));
			}

		}

		return lGeom;
	}

	private static IRing generateProjectedRing(IDirectPositionList dpl,
			IDirectPositionList dplProj, IRing r, Vecteur v) {

		IDirectPositionList dplRing = r.coord();

		IDirectPositionList dplProjRing = new DirectPositionList();

		for (IDirectPosition posRing : dplRing) {

			int index = dpl.getList().indexOf(posRing);

			if (index == -1) {
				
				return null;
			}

			IDirectPosition projectedPos = dplProj.get(index);

			if (projectedPos == null) {
				return null;
			}

			dplProjRing.add(projectedPos);

		}

		

		ApproximatedPlanEquation aPE = new ApproximatedPlanEquation(dplProjRing);

		if (aPE.getNormale().prodScalaire(v) < 0 ) {

			dplProjRing = dplProjRing.reverse();

		}

		

		return new GM_Ring(new GM_LineString(dplProjRing));

	}

	private static List<IGeometry> generatePolygones(IDirectPositionList dpl,
			IDirectPositionList dplProj, IFeature feat) {

		List<IGeometry> lGeom = new ArrayList<IGeometry>();

		List<IOrientableSurface> lOS = FromGeomToSurface.convertGeom(feat
				.getGeom());

		bouclesurface: for (IOrientableSurface os : lOS) {

			if (!(os instanceof IPolygon)) {
				continue;
			}

			IPolygon poly = (IPolygon) os;
			
			
			
			IRing exteriorProjected =  generateProjectedRing(dpl,dplProj, poly.getExterior(), new Vecteur(0,0,1));
			
			if(exteriorProjected == null){
				continue;
			}
			
			IPolygon projectedPoly = new GM_Polygon(exteriorProjected);
			
			
			
			for(IRing r:poly.getInterior()){
				
				
				IRing interiorProjected =  generateProjectedRing(dpl,dplProj, r, new Vecteur(0,0,-1));
				
				
				if(interiorProjected == null){
					continue bouclesurface;
				}
				
				projectedPoly.addInterior(interiorProjected);
				
				
			}
			
			lGeom.add(projectedPoly);

		}

		return lGeom;
	}
	
	
	private static List<IGeometry> generateLateralGeom(IDirectPositionList dpl, IDirectPositionList dplProj, IRing r, boolean isExterior){
		
		List<IGeometry> lGeomOut = new ArrayList<IGeometry>();
		
		int nbP = r.coord().size();
		
		for(int i=isExterior?0:nbP;isExterior? i<nbP : i>=0 ;i=isExterior?i+1:i-1){
			
			if(i== (isExterior?0:nbP-1)){
				continue;
			}
			
			
			
			IDirectPosition dp = r.coord().get(i);
			IDirectPosition dpPred = r.coord().get(isExterior?i-1:i+1);
			
			
			int index = dpl.getList().indexOf(dp);
			
			
			if(index == -1){
				continue;
			}
			
			int indexPred = dpl.getList().indexOf(dpPred);
			
			
			if(indexPred == -1){
				continue;
			}
			
			IDirectPosition dpProj = dplProj.get(index);
			
			if(dpProj == null){
				continue;
			}

			IDirectPosition dpProjPred = dplProj.get(indexPred);
			
			if(dpProjPred == null){
				continue;
			}
			
			
			
			
			IDirectPositionList dplLat = new DirectPositionList();
			dplLat.add(dp);
			dplLat.add(dpProj);
			dplLat.add(dpProjPred);
			dplLat.add(dpPred);
			dplLat.add(dp);
			
			
			lGeomOut.add(new GM_Polygon(new GM_LineString(dplLat)));
		}
		
		
		return lGeomOut;
		
	}
	
	
	private static Collection<? extends IGeometry> generateVolume(
			IDirectPositionList dpl, IDirectPositionList dplProj, IFeature feat, Vecteur v) {
		
		
		List<IGeometry> lGeom = new ArrayList<IGeometry>();

		List<IOrientableSurface> lOS = FromGeomToSurface.convertGeom(feat
				.getGeom());

		bouclesurface: for (IOrientableSurface os : lOS) {

			if (!(os instanceof IPolygon)) {
				continue;
			}

			IPolygon poly = (IPolygon) os;
			
			
			//On inverse le polygon pour qu'il ait sa normale dans le sens inverse du vecteur de projection
			ApproximatedPlanEquation aPE = new ApproximatedPlanEquation(poly);
			
			
			if(aPE.getNormale().prodScalaire(v.multConstante(-1)) < 0){
				poly = (IPolygon) poly.reverse();
			}
			
			
			
			
		
			IRing exteriorProjected =  generateProjectedRing(dpl,dplProj, poly.getExterior(), v);
			

			if(exteriorProjected == null){
				continue;
			}
			
			List<IGeometry> lGTemp = generateLateralGeom(dpl,dplProj,poly.getExterior(),true);
			
			if(lGTemp == null) {
				continue;
			}
			
			lGeom.addAll(lGTemp);
			
			IPolygon projectedPoly = new GM_Polygon(exteriorProjected);
			
			
			
			for(IRing r:poly.getInterior()){
				
				
				IRing interiorProjected =  generateProjectedRing(dpl,dplProj, r,  v.multConstante(-1));
				
				
				if(interiorProjected == null){
					continue bouclesurface;
				}
				
				projectedPoly.addInterior(interiorProjected);
				
				 lGTemp = generateLateralGeom(dpl,dplProj,poly.getExterior(),false);
				 
					if(lGTemp == null) {
						continue bouclesurface;
					}
					
					lGeom.addAll(lGTemp);
				
				
			}
			
			
			//On ajoute les extrémités
			lGeom.add(projectedPoly);
			lGeom.add(poly);
		}

		return lGeom;
		
		
	}

	private static IDirectPositionList preprocessPoint(IFeature feat,Vecteur v){
		IDirectPositionList dpl = new DirectPositionList();
		
		List<IOrientableSurface>iOrientableSurfaces = FromGeomToSurface.convertGeom(feat.getGeom());
		
		
		
		
		
		for(IOrientableSurface os : iOrientableSurfaces){
			ApproximatedPlanEquation ape = new ApproximatedPlanEquation(os);
			
			
			if(ape.getNormale().prodScalaire(v) < 0){
				continue;
			}
			
			
			IDirectPositionList dpTemp = os.coord();
			
			
			for(IDirectPosition dp:dpTemp){
				
				if(! dpl.contains(dp)){
					dpl.add(dp);
				}
				
			}
			
			
			
		}
		
		
		
		
		
		
		
		return dpl;
	}
	
	
	private static IDirectPositionList removeDouble(IDirectPositionList dplIn) {
		IDirectPositionList dplOut = new DirectPositionList();

		

		for (IDirectPosition dp : dplIn) {

			if (!dplOut.contains(dp)) {
				dplOut.add(dp);
			}
		}

	

		return dplOut;

	}

	private static List<IGeometry> orderByDistance(List<IGeometry> lG, IPoint p) {

		List<IGeometry> lP = new ArrayList<IGeometry>();
		List<Double> lD = new ArrayList<Double>();

		bfor: for (IGeometry parc : lG) {

			double d = p.distance(parc);

			for (int i = 0; i < lP.size(); i++) {

				if (d < lD.get(i)) {

					lD.add(i, d);
					lP.add(i, parc);
					continue bfor;
				}

			}

			lD.add(d);
			lP.add(parc);

		}

		return lP;
	}

}
