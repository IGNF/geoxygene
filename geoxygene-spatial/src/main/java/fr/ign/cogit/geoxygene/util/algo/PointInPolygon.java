/**
 * 
 * This software is released under the licence CeCILL
 * 
 * see LICENSE.TXT
 * 
 * see <http://www.cecill.info/ http://www.cecill.info/
 * 
 * 
 * 
 * @copyright IGN
 * 
 * @author Brasebin Mickaël
 * 
 * @version 1.0
 **/
package fr.ign.cogit.geoxygene.util.algo;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IEnvelope;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;

/**
 * 
 * Generate a point in a polygon
 * 
 * @author MBrasebin
 *
 */
public class PointInPolygon {

	private static int IT_MAX = 10000;

	public static IDirectPosition get(IPolygon poly) {

		IEnvelope env = poly.getEnvelope();

		int count = 0;

		double x = 0, y = 0;

		while (true && count < IT_MAX) {

			count++;
			x = Math.random() * (env.getUpperCorner().getX() - env.getLowerCorner().getX())
					+ env.getLowerCorner().getX();
			y = Math.random() * (env.getUpperCorner().getY() - env.getLowerCorner().getY())
					+ env.getLowerCorner().getY();

			IDirectPosition dp = new DirectPosition(x, y);

			if (poly.contains(new GM_Point(dp).buffer(0.05))) {
				return dp;
			}

		}

		return new DirectPosition(x, y);

	}
}
