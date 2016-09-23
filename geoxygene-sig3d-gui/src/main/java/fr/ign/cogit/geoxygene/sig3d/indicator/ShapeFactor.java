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
 * @version 1.0
 **/
package fr.ign.cogit.geoxygene.sig3d.indicator;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.sig3d.calculation.OrientedBoundingBox;

/**
 * Hauteur bâtiment / largeur + longueur
 * 
 * @author MBrasebin
 *
 */
public class ShapeFactor {

	private double value;

	/**
	 * 
	 * @param bP
	 */
	public ShapeFactor(IFeature bP) {

		OrientedBoundingBox oBB = new OrientedBoundingBox(bP.getGeom());

		IPolygon poly = oBB.getPoly();

		value = 0;

		if (poly != null) {
			double h = oBB.getzMax() - oBB.getzMin();

			IDirectPositionList dpl = poly.coord();
			IDirectPosition dp1 = dpl.get(0);
			IDirectPosition dp2 = dpl.get(1);
			IDirectPosition dp3 = dpl.get(2);

			double d = dp1.distance2D(dp2);
			double w = dp2.distance(dp3);

			value = h * 2 / (w + d);

		}

	}

	public Double getValue() {

		return value;
	}

}
