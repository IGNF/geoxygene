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
import fr.ign.cogit.geoxygene.convert.FromGeomToSurface;
import fr.ign.cogit.geoxygene.sig3d.calculation.Calculation3D;
import fr.ign.cogit.geoxygene.sig3d.convert.geom.FromPolygonToTriangle;

/**
 * 
 * @author MBrasebin
 *
 */
public class RoofArea {

	private double value = 0;

	public RoofArea(IFeature roof) {

		value = Calculation3D
				.area(FromPolygonToTriangle.convertAndTriangle(FromGeomToSurface.convertGeom(roof.getGeom())));

	}

	public Double getValue() {

		return value;
	}

}
