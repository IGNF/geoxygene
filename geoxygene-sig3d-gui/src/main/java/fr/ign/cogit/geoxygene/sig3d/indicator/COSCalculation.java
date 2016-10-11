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

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;

/**
 * 
 * Calcul du COS
 * 
 * @author MBrasebin
 *
 */
public class COSCalculation {

	public static enum METHOD {
		SIMPLE, FLOOR_CUT
	}

	public static double assess(List<? extends IFeature> lBuildings, METHOD m, double area) {

		double aireBatie = 0;

		switch (m) {

		case SIMPLE:
			aireBatie = SHONCalculation.assessSimpleAireBati(lBuildings);
			break;
		case FLOOR_CUT:
			aireBatie = SHONCalculation.assessAireBatieFromCut(lBuildings);
			break;
		}

		return aireBatie / area;
	}

}
