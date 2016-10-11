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
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;
import fr.ign.cogit.geoxygene.convert.FromGeomToSurface;
import fr.ign.cogit.geoxygene.sig3d.calculation.Calculation3D;
import fr.ign.cogit.geoxygene.sig3d.calculation.Util;
import fr.ign.cogit.geoxygene.sig3d.convert.geom.FromPolygonToTriangle;

/**
 * 
 * Calcul de la surface de la facade
 * 
 * @author MBrasebin
 *
 */
public class FacadeArea {

	private double value = 0;

	public FacadeArea(IFeature feat) {
		IMultiSurface<IOrientableSurface> ims = Util.detectVertical(FromGeomToSurface.convertGeom(feat.getGeom()), 0.2);
		for (IOrientableSurface f : ims) {

			value = value + Calculation3D.area(FromPolygonToTriangle.convertAndTriangle(f));
		}
	}

	public double getValue() {
		return value;
	}

}
