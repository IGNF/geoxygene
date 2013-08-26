package fr.ign.cogit.geoxygene.contrib.quality.estim.spatialcontext.grid.gridcat;

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
public class GeometryFactory {

	/**
	 * Creates a Circle polygon from its centre and radius. Uses a buffer to 
	 * create the circle so the number of segments of the polygon is required.
	 * @param centre the centre of the polygon circle
	 * @param radius the radius of the circle
	 * @param nbSegments the number of segments of the polygon circle
	 * @return the IPolygon circle geometry 
	 */
	public static GM_Polygon buildCircle(DirectPosition centre, double radius, 
			int nbSegments){
		return (GM_Polygon) centre.toGM_Point().buffer(radius, nbSegments);
	}
	

}

