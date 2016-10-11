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
import fr.ign.cogit.geoxygene.sig3d.analysis.roof.RoofDetection;
import fr.ign.cogit.geoxygene.sig3d.calculation.Calculation3D;
import fr.ign.cogit.geoxygene.sig3d.calculation.OrientedBoundingBox;
import fr.ign.cogit.geoxygene.sig3d.convert.geom.FromPolygonToTriangle;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Solid;

/**
 * 
 * @author MBrasebin
 *
 */
public class ShapeDeviation {

	private double value;

	/**
	 * Rapport entre le volume d'un objet de le volume de sa boite orientée
	 * 
	 * @param bP
	 */
	public ShapeDeviation(IFeature bP) {

		value = 1;

		OrientedBoundingBox oBB = new OrientedBoundingBox(bP.getGeom());

		if (oBB.getPoly() != null) {

			double zMin = oBB.getzMin();
			double zMax = oBB.getzMax();

			double volArea = oBB.getPoly().area() * (zMax - zMin);

			if (volArea == 0) {
				return;
			}
			
			IMultiSurface<? extends IOrientableSurface> ims = RoofDetection.detectRoof(bP, 0.2, false);

			double vBati = Calculation3D.volume(new GM_Solid(FromPolygonToTriangle.convertAndTriangle(ims.getList()	)));

			value = vBati / volArea;

			if (value > 1) {

				System.out.println("Why ?" + value);
			}

		}

	}

	public Double getValue() {

		return value;
	}

}
