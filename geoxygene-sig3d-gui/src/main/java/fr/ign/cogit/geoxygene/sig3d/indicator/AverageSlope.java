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
import fr.ign.cogit.geoxygene.contrib.geometrie.Vecteur;
import fr.ign.cogit.geoxygene.convert.FromGeomToSurface;
import fr.ign.cogit.geoxygene.sig3d.equation.ApproximatedPlanEquation;
import fr.ign.cogit.geoxygene.sig3d.semantic.DTMArea;

/**
 * 
 * Calcul de la pente moyenne sur une parcelle en fonction d'un DTM
 * 
 * @author MBrasebin
 *
 */
public class AverageSlope {

	public static double averageSlope(IFeature feat,DTMArea dtm ){
		try {
			return averageSlope(FromGeomToSurface.convertMSGeom(feat.getGeom()), dtm);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Double.NaN;
	}

	public static double averageSlope(IMultiSurface<IOrientableSurface> os, DTMArea dtm) throws Exception {

		Vecteur vTot = new Vecteur(0, 0, 0);

		for (IOrientableSurface o : os) {

			double area3DTemp = dtm.calcul3DArea(o);

			ApproximatedPlanEquation ep = new ApproximatedPlanEquation(o);

			Vecteur v = ep.getNormale();

			v.normalise();

			if (v.getZ() < 0) {
				v.multConstante(-area3DTemp);
			} else {
				v.multConstante(area3DTemp);
			}

			vTot = vTot.ajoute(v);

		}

		double z = vTot.getZ();

		vTot.setZ(0);

		double norme = vTot.norme();

		if (norme == 0) {
			return 0;
		}

		return Math.PI / 2 - Math.atan(z / norme);

	}

}
