/**
 * 
 * This software is released under the licence CeCILL
 * 
 * see LICENSE.TXT
 * 
 * see <http://www.cecill.info/ http://www.cecill.info/
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
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.sig3d.analysis.roof.RoofDetection;
import fr.ign.cogit.geoxygene.sig3d.geometry.Box3D;

/**
 * 
 * Calcul de la hauteur d'un bâtiment
 * 
 * @author MBrasebin
 *
 */
public class HauteurCalculation {


	// //////////////////DIFFERENTS TYPES DE ZHAUT
	// // IL s'agit d'un Z et pas d'un H bien sur
	public static double calculateZHautPPE(IFeature b) {

		double hauteurParEtage = StoreyCalculation.process(b);

		if (hauteurParEtage <= 0 || !StoreyCalculation.USE_STOREYS_HEIGH_ATT) {
			hauteurParEtage = StoreyCalculation.HAUTEUR_ETAGE;
		}

		int nbEtage = StoreyCalculation.process(b);

		double hauteur = hauteurParEtage * nbEtage;

		Box3D box = new Box3D(b.getGeom());

		return hauteur + box.getLLDP().getZ();
	}

	public static double calculateZhautMinRoof(IFeature b) {

		Box3D box = new Box3D(RoofDetection.detectRoof(b, 0.2, false));

		return box.getLLDP().getZ();
	}

	public static double calculateZHautPHF(IFeature b) {
		Box3D box = new Box3D(b.getGeom());
		return box.getURDP().getZ();
	}

	// //////////////////DIFFERENTS TYPES DE ZBAS

public static double calculateZBasPHT(List<IGeometry> parcelGeom) {

		double zMax = Double.NEGATIVE_INFINITY;

		for (IGeometry geom : parcelGeom) {

			Box3D box = new Box3D(geom);

			zMax = Math.max(zMax, box.getLLDP().getZ());

		}

		return zMax;
	}
	public static double calculateZBasPBT(List<IGeometry> parcelGeom) {

		double zMin = Double.POSITIVE_INFINITY;

		for (IGeometry sp : parcelGeom) {

			Box3D box = new Box3D(sp);

			zMin = Math.min(zMin, box.getLLDP().getZ());

		}

		return zMin;
	}
	public static double calculateZBasPBB(IFeature b) {
		Box3D box = new Box3D(b.getGeom());
		return box.getLLDP().getZ();
	}
	public static double calculateZBasEP(IFeature b, List<IGeometry> geomBoundary) {

		double zMin = Double.POSITIVE_INFINITY;

		for (IGeometry bord : geomBoundary) {

			Box3D box = new Box3D(bord);
			zMin = Math.min(zMin, box.getLLDP().getZ());

		}

		if (zMin == Double.POSITIVE_INFINITY) {
			zMin = calculateZBasPBB(b);
		}

		return zMin;
	}

}
