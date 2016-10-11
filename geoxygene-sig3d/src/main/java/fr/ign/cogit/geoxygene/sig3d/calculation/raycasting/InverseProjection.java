package fr.ign.cogit.geoxygene.sig3d.calculation.raycasting;

import java.util.ArrayList;
import java.util.List;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ITriangle;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.geometrie.Vecteur;
import fr.ign.cogit.geoxygene.convert.FromGeomToSurface;
import fr.ign.cogit.geoxygene.sig3d.convert.geom.FromPolygonToTriangle;
import fr.ign.cogit.geoxygene.sig3d.equation.LineEquation;
import fr.ign.cogit.geoxygene.sig3d.equation.PlanEquation;
import fr.ign.cogit.geoxygene.sig3d.geometry.Box3D;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;

/**
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
 *
 */
public class InverseProjection {

	public static List<IGeometry> process(
			IFeatureCollection<? extends IFeature> lBatiments,
			double sursampling, Vecteur v, double prof) {

		v.normalise();

		List<IGeometry> lGeom = new ArrayList<IGeometry>();

		IDirectPositionList shaddedPoint = new DirectPositionList();

		// Préparation des faces faisant dos au soleil, celles pouvant arrêter
		// la lumière

		List<List<ITriangle>> lLOSFaceSoleil = new ArrayList<List<ITriangle>>();
		List<List<ITriangle>> lLOSDosSoleil = new ArrayList<List<ITriangle>>();
		List<Box3D> lBox3D = new ArrayList<Box3D>();
		// Pour chaque objet
		for (IFeature feat : lBatiments) {

			List<ITriangle> lOSFaceSoleil = new ArrayList<ITriangle>();
			List<ITriangle> lOSDosSoleil = new ArrayList<ITriangle>();

			lLOSFaceSoleil.add(lOSFaceSoleil);
			lLOSDosSoleil.add(lOSDosSoleil);

			lBox3D.add(new Box3D(feat.getGeom()));

			// On récupère les géométries qui font face

			List<IOrientableSurface> lOS = FromGeomToSurface.convertGeom(feat
					.getGeom());

			List<ITriangle> lTri = FromPolygonToTriangle
					.convertAndTriangle(lOS);

			for (ITriangle tri : lTri) {

				// On a des sommets à considérer
				// Si la géométrie est dos au vecteur, tous les points sont
				// sombres

				PlanEquation pE = new PlanEquation(tri);

				if (pE.getNormale().prodScalaire(v) > 0) {
					IDirectPositionList dplSur = surEchantillonne(tri,
							sursampling);
					shaddedPoint.addAll(dplSur);
					lOSDosSoleil.add(tri);
				} else {
					lOSFaceSoleil.add(tri);
				}

			}

		}

		// Maintenant il reste à vérifier pour chaque géométrie côté lumière de
		// chaque bâtiment si elle arrrive
		int nbBatiment = lBatiments.size();

		for (int i = 0; i < nbBatiment; i++) {

			List<Integer> idsConcernedBuilding = concernedBatiment(
					lBox3D.get(i), lBox3D, v, prof);

			List<ITriangle> lFaceSol = lLOSFaceSoleil.get(i);

			for (ITriangle triSol : lFaceSol) {

				IDirectPositionList dplSur = surEchantillonne(triSol,
						sursampling);

				for (IDirectPosition dpSur : dplSur) {

					boolean interFound = false;

					for (Integer id : idsConcernedBuilding) {

						LineEquation le = new LineEquation(dpSur, v);
						if (intersectionVecteur(dpSur, le,
								lLOSDosSoleil.get(id))) {

							interFound = true;
							break;
						}

					}

					if (interFound) {
						 shaddedPoint.add(dpSur);
					}

				}

			}

		}

		for (IDirectPosition dp : shaddedPoint) {
			lGeom.add(new GM_Point(dp));
		}

		return lGeom;

	}

	private static boolean intersectionVecteur(IDirectPosition dpDep,
			LineEquation le, List<ITriangle> listTriangles) {

		for (ITriangle tri : listTriangles) {

			IDirectPosition dp = RayCasting.intersectionPolygonLine(le, tri);

			if (dp == null) {
				continue;
			}

			return true;

		}

		return false;
	}

	private static List<Integer> concernedBatiment(Box3D b, List<Box3D> lBox3D,
			Vecteur v, double prof) {

		int nbBox = lBox3D.size();

		List<Integer> lFeatOut = new ArrayList<Integer>();

		for (int i = 0; i < nbBox; i++) {

			Box3D bTemp = lBox3D.get(i);

			double dist = bTemp.to_2D().distance(b.to_2D());

			if (dist > prof) {
				continue;
			}

			if (bTemp.getURDP().getZ() < b.getLLDP().getZ()
					+ Math.abs(v.getZ() * prof)) {

				continue;

			}

			Vecteur vCentre = new Vecteur(b.getCenter(), bTemp.getCenter());

			if (vCentre.prodScalaire(v) > 0) {
				continue;
			}

			lFeatOut.add(i);

		}

		return lFeatOut;

	}

	private static IDirectPositionList surEchantillonne(ITriangle tri,
			double surechantillone) {

		  
//		  IDirectPosition p1 = tri.coord().get(0); 
//		  IDirectPosition p2 =  tri.coord().get(1);
//		  IDirectPosition p3 = tri.coord().get(2);
		 
		  
		  // On prend 2 indices au hasard 
//		  Vecteur v1 = new Vecteur(p1,p2);
		  
//		  double normeV1 = v1.norme();
//		  double pasX = normeV1 / surechantillone;
		  
		  
//		  Vecteur v2 = new Vecteur(p1,p3);
		  
//		  double normeV2 = v2.norme();
//		  double pasY = normeV2 / surechantillone;
		  
		  
		  IDirectPositionList dplOut = new DirectPositionList();
		  
		  //On s'assure que les sommes
		  
		  double x = tri.coord().get(0).getX() + tri.coord().get(1).getX() +  tri.coord().get(2).getX();
		  double y = tri.coord().get(0).getY() + tri.coord().get(1).getY() +  tri.coord().get(2).getY();
		  double z = tri.coord().get(0).getZ() + tri.coord().get(1).getZ() +  tri.coord().get(2).getZ();
		  
		  dplOut.add(new DirectPosition(x/3,y/3,z/3));
		  
		  
		  
		  
		  
		  
		  
		  // Cette méthode est conseillé dans l'article dont est issue la   mesure
		  
		  
		  
		  /*
		  
		  for(int i=0; i*pasX < v1.norme();i++){
			  for(int j=0; i*pasY < v2.norme();j++){
				  	
				  double aleaX = i * pasX / normeV1;
				  double aleaY = j * pasY /normeV2;
				  
				    DirectPosition pointFinal = new DirectPosition(p1.getX()
				            * (1 - Math.sqrt(aleaX)) + p2.getX() * Math.sqrt(aleaX) * (1 - aleaY)
				            + p3.getX() * aleaY * Math.sqrt(aleaX), p1.getY()
				            * (1 - Math.sqrt(aleaX)) + p2.getY() * Math.sqrt(aleaX) * (1 - aleaY)
				            + p3.getY() * aleaY * Math.sqrt(aleaX), p1.getZ()
				            * (1 - Math.sqrt(aleaX)) + p2.getZ() * Math.sqrt(aleaX) * (1 - aleaY)
				            + p3.getZ() * aleaY * Math.sqrt(aleaX));
				    
				    dplOut.add(pointFinal);
				  
			  }
		  }*/


		return dplOut;
	}
}
