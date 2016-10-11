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

import java.util.List;

import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;

/**
 * Calcul du Coefficient d'Emprise au Sol
 * 
 * @see http://blog.logic-immo.com/2010/01/questions-argent-droit/urbanisme-cos-
 *      ces-plu-pos-shon-shob-definitions-architecte/
 * 
 * @author MBrasebin
 *
 */
public class CESCalculation {

	public static double assess(IGeometry parcelleSurface, List<IGeometry> buildingFootprint) {

		double area = parcelleSurface.area();
		double aireBatie = 0;

		for (IGeometry bF : buildingFootprint) {
			aireBatie = aireBatie + bF.area();
		}

		return aireBatie / area;

	}
}
