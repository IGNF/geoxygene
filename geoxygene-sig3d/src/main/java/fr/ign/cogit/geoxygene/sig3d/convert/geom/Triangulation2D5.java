package fr.ign.cogit.geoxygene.sig3d.convert.geom;

import java.util.List;

import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ITriangle;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Face;
import fr.ign.cogit.geoxygene.contrib.delaunay.TriangulationJTS;
import fr.ign.cogit.geoxygene.sig3d.equation.ApproximatedPlanEquation;
import fr.ign.cogit.geoxygene.sig3d.topology.TriangulationLoader;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Triangle;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiSurface;

/**
 * 2D5 triangulation (based on JTS so it works only with 3D geometries that are
 * valid in 2D) For real triangulation, take a look at Tetraherisation class
 * (but requires native lib)
 * 
 * @author mbrasebin
 *
 */
public class Triangulation2D5 {

	public static IMultiSurface<ITriangle> triangulate(IMultiSurface<IOrientableSurface> surf)
			{
		return triangulateFromList(surf.getList());

	}
	
	public static IMultiSurface<ITriangle> triangulateFromList(List<IOrientableSurface> surf){

		// Les surfaces qui constitueront le bâtiment final
		IMultiSurface<ITriangle> finalTrianglatedSurface = new GM_MultiSurface<ITriangle>();

		for(IOrientableSurface s: surf) {
			finalTrianglatedSurface.addAll(triangulateFromSurface(s));
		}
		
		
		return finalTrianglatedSurface;
	}
	
	
	public static IMultiSurface<ITriangle> triangulateFromSurface(IOrientableSurface surf){
		IMultiSurface<ITriangle> finalTrianglatedSurface = new GM_MultiSurface<ITriangle>();

		TriangulationJTS triJTS = TriangulationLoader.generateFromSurface(surf);

		try {

			// /On tente la triangulation

			triJTS.triangule("");

		} catch (Exception e) {
			e.printStackTrace();

		}

		IPopulation<Face> popFaces = triJTS.getPopFaces();
		
		ApproximatedPlanEquation aPE = new ApproximatedPlanEquation(surf);

		IGeometry buffer = surf.buffer(0.5);
		// On traite chaque triangle
		for (Face f : popFaces) {
			
		
		if (! buffer.contains(f.getGeom())) {
			continue;
		}
	

			IOrientableSurface geom = f.getGeometrie();
			IDirectPositionList pointTri = geom.coord();
			
			for(IDirectPosition p: pointTri) {
				
				
					p.setZ(aPE.getZ(p));
				
			}
			
			

			
			ITriangle t = new  GM_Triangle(pointTri.get(0), pointTri.get(1), pointTri.get(2));
			
			
			
			
			finalTrianglatedSurface.add(t);
		}
		return finalTrianglatedSurface;
	}
}
