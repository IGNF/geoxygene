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

/**
 * 
 * @author MBrasebin
 *
 */
public class RoofCoeff {

	private double value = 0;

	public RoofCoeff(IFeature b) {
		
	 double zMinRoof =	HauteurCalculation.calculateZhautMinRoof(b);
	 double heighestZ =	HauteurCalculation.calculateZHautPHF(b);
		
		


		value = heighestZ / zMinRoof - 1;

	}

	public Double getValue() {

		return value;
	}

}
