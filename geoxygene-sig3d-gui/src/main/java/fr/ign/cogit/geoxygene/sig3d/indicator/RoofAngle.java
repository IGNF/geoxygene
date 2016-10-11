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
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;
import fr.ign.cogit.geoxygene.convert.FromGeomToSurface;
import fr.ign.cogit.geoxygene.sig3d.calculation.AngleFromSurface;

/**
 * 
 * @author MBrasebin
 *
 */
public class RoofAngle {

	public static double angleMin(IFeature roof) {

		double angleMin = Double.POSITIVE_INFINITY;
		for (IOrientableSurface o : FromGeomToSurface.convertGeom(roof.getGeom())) {

			angleMin = Math.min(angleMin, AngleFromSurface.calculate(o));
		}

		return angleMin;

	}

	public static double angleMax(IFeature roof) {

		double angleMax = Double.NEGATIVE_INFINITY;

		for (IOrientableSurface o : FromGeomToSurface.convertGeom(roof.getGeom())) {

			angleMax = Math.max(angleMax, AngleFromSurface.calculate(o));
		}

		return angleMax;

	}

}
