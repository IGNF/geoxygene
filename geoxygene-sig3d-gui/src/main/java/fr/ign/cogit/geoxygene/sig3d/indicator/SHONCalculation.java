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

import java.util.ArrayList;
import java.util.List;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.convert.FromGeomToSurface;
import fr.ign.cogit.geoxygene.sig3d.calculation.CutBuilding;
import fr.ign.cogit.geoxygene.sig3d.geometry.Box3D;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiSurface;

/**
 * 
 * Calcul de la SHON (Surface hors œuvre nette)
 * 
 * @author MBrasebin
 *
 */
public class SHONCalculation {

	// METHODE
	public static double COEF_PLANCHER_PAR_ETAGE = 0.8;

	// COMPLEXE
	public static double HABITABLE_HAUTEUR = 1.8;

	public static enum METHOD {
		SIMPLE, FLOOR_CUT
	}

	public static double assess(List<? extends IFeature> lB, METHOD m) {

		double aireBatie = 0;

		switch (m) {

		case SIMPLE:
			aireBatie = SHONCalculation.assessSimpleAireBati(lB);
		case FLOOR_CUT:
			aireBatie = SHONCalculation.assessAireBatieFromCut(lB);
		}

		return aireBatie;
	}

	public static double assessSimpleAireBati(List<? extends IFeature> lBP) {

		double aireBatie = 0;

		for (IFeature b : lBP) {

			aireBatie = aireBatie + assessSimpleSHON(b);
		}

		return aireBatie;

	}

	public static double assessAireBatieFromCut(List<? extends IFeature> lBP) {

		double aireBatie = 0;

		for (IFeature b : lBP) {

			aireBatie = aireBatie + assessCUTSHON(b);
		}

		return aireBatie;

	}

	public static double assessSimpleSHON(IFeature bati) {

		double aireBatie = 0;

		int nbEtage = StoreyCalculation.process(bati);

		aireBatie = bati.getGeom().area() * nbEtage * COEF_PLANCHER_PAR_ETAGE;

		return aireBatie;
	}

	public static List<IOrientableSurface> DEBUG = new ArrayList<IOrientableSurface>();

	public static double assessCUTSHON(IFeature bati) {

		double hauteurEtage = 0;

		if (StoreyCalculation.USE_STOREYS_HEIGH_ATT) {
			hauteurEtage = StoreyCalculation.HAUTEUR_ETAGE;
		}

		if (hauteurEtage < 0.1) {
			hauteurEtage = StoreyCalculation.HAUTEUR_ETAGE;
		}

		List<IOrientableSurface> lOS = new ArrayList<IOrientableSurface>();
		lOS.addAll(FromGeomToSurface.convertGeom(bati.getGeom()));

		Box3D b = new Box3D(new GM_MultiSurface<>(lOS));

		double zMin = b.getLLDP().getZ();
		double zMax = b.getURDP().getZ();

		int nbFloor = StoreyCalculation.process(bati);

		if (nbFloor == 0) {
			nbFloor = 1;
		}

		double zActu = zMin;
		double areaSHON = 0;

		for (int i = 0; i < nbFloor; i++) {

			List<IPolygon> lG = CutBuilding.cutAt(lOS, zActu + HABITABLE_HAUTEUR);

			double areaTemp = calculAreaFromCut(lG);

			if (areaTemp == -1) {
				return -1;
			}

			DEBUG.addAll(lG);

			zActu = zActu + hauteurEtage;

			areaSHON = areaSHON + areaTemp;

		}

		if (areaSHON == 0 && nbFloor == 0 && (zMax - zMax) > HABITABLE_HAUTEUR) {

			List<IPolygon> lG = CutBuilding.cutAt(lOS, (zMax + zMax) / 2);

			DEBUG.addAll(lG);

			double areaTemp = calculAreaFromCut(lG);

			areaSHON = areaTemp;

		}

		return areaSHON * COEF_PLANCHER_PAR_ETAGE;

	}

	/**
	 * 
	 * @param lG
	 * @return
	 */
	private static double calculAreaFromCut(List<IPolygon> lG) {
		double areaTemp = 0;

		if (lG == null) {
			return -1;
		}

		int nbGeom = lG.size();
		for (int j = 0; j < nbGeom; j++) {

			IGeometry gT = lG.get(j);

			if (gT instanceof IPolygon) {
				if (gT.isValid()) {
					areaTemp = areaTemp + gT.area();
				} /*
					 * else { return -1;
					 * 
					 * }
					 */

			} /*
				 * else { areaTemp = -1; return areaTemp; }
				 */

		}

		return areaTemp;
	}
}
