package fr.ign.cogit.geoxygene.sig3d.analysis;

import java.util.ArrayList;
import java.util.List;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.sig3d.equation.PlanEquation;
import fr.ign.cogit.geoxygene.sig3d.geometry.topology.Tetraedre;
import fr.ign.cogit.geoxygene.sig3d.geometry.topology.Triangle;
import fr.ign.cogit.geoxygene.sig3d.tetraedrisation.TetraedrisationTopo;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_OrientableSurface;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Solid;

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
 * @version 1.7
 * 
 *          Classe permettant de créer une forme à partir d'un nuage de points
 * 
 */
public class AlphaShape {

	public static double alpha = 500;

	public static GM_Solid generateSol(IDirectPositionList dpl) {
		/*
		 * DirectPositionList dpl = new DirectPositionList(); for (int i = 0; i
		 * < 100; i++) { double teta = 2 * Math.PI * Math.random(); double phi =
		 * 2 * Math.PI * Math.random(); DirectPosition dp = new
		 * DirectPosition(Math.cos(teta) Math.cos(phi) * 100, Math.sin(teta) *
		 * Math.cos(phi) 100, Math.sin(phi) * 100); dpl.add(dp); }
		 */

		GM_LineString ls = new GM_LineString(dpl);
		GM_OrientableSurface os = new GM_Polygon(ls);

		List<IOrientableSurface> lOS = new ArrayList<IOrientableSurface>();
		lOS.add(os);

		GM_Solid sol = new GM_Solid(lOS);

		return sol;

	}

	private static IDirectPosition determineSphereCenter(Triangle tri, double rayon) {
		return AlphaShape.determineSphereCenter(tri.getCorners(0).getDirect(), tri.getCorners(1).getDirect(),
				tri.getCorners(2).getDirect(), rayon);
	}

	/**
	 * On calcul le centre d'une sphère passant par 3 points et de rayon rayon.
	 * Ce calcul est obtenu grâce à l'intersection entre les sphères passant par
	 * chacun des sommets. On prend la sphère se trouvant dans le demi espace
	 * positif
	 * 
	 * @param p1
	 * @param p2
	 * @param p3
	 * @param rayon
	 * @return Renvoie un DirectPosition correspondant au centre d'une sphère
	 *         passant par 3 points et de rayon rayon
	 */
	private static IDirectPosition determineSphereCenter(IDirectPosition dp1, IDirectPosition dp2, IDirectPosition dp3,
			double rayon) {
		// On initialise les paramètres connus
		double x1 = dp1.getX();
		double y1 = dp1.getY();
		double z1 = dp1.getZ();

		double x2 = dp2.getX();
		double y2 = dp2.getY();
		double z2 = dp2.getZ();

		double x3 = dp3.getX();
		double y3 = dp3.getY();
		double z3 = dp3.getZ();

		// Intersection S1 S2 Equation plan de la forme a1x + b1y + c1z + d1 = 0
		double a1 = 2 * (x2 - x1);
		double b1 = 2 * (y2 - y1);
		double c1 = 2 * (z2 - z1);
		double d1 = x1 * x1 - x2 * x2 + y1 * y1 - y2 * y2 + z1 * z1 - z2 * z2;

		// Intersection S2 S3 Equation plan de la forme a2x + b2y + c2z + d2 = 0
		double a2 = 2 * (x3 - x1);
		double b2 = 2 * (y3 - y1);
		double c2 = 2 * (z3 - z1);
		double d2 = x1 * x1 - x3 * x3 + y1 * y1 - y3 * y3 + z1 * z1 - z3 * z3;

		// On trouve en posant z = t
		// y(t) = p1 + p2 * t
		double div1 = a2 * b1 - a1 * b2;
		double p1 = (a1 * d2 - a2 * d1) / div1;
		double p2 = (a1 * c2 - a2 * c1) / div1;

		// x(t) = p3 + p4 * t
		// En replaçant dans la première équation
		double p3 = -(b1 * p1 + d1) / a1;
		double p4 = -(b1 * p2 + c1) / a1;

		// On remplace dans une équation de sphère x et y
		double m1 = p3 - x1;
		double m2 = p1 - y1;

		// La solution est celle de la forme : H1 * t^2 + H2 * t +H3 = 0
		double h1 = p4 * p4 + p2 * p2 + 1;
		double h2 = 2 * (p4 * m1 + p2 * m2 - z1);
		double h3 = m1 * m1 + m2 * m2 + z1 * z1 - rayon * rayon;

		// résolution équation second degré
		double delta = h2 * h2 - 4 * h1 * h3;

		if (delta < 0) {
			return null;
		}

		double t0 = (-h2 - Math.sqrt(delta)) / (2 * h1);

		double t1 = (-h2 + Math.sqrt(delta)) / (2 * h1);

		double xFinal = p3 + p4 * t1;
		double yFinal = p1 + p2 * t1;
		double zFinal = t1;

		DirectPosition pt1 = new DirectPosition(xFinal, yFinal, zFinal);

		if (delta == 0) {
			return pt1;
		}

		xFinal = p3 + p4 * t0;
		yFinal = p1 + p2 * t0;
		zFinal = t0;

		DirectPosition pt0 = new DirectPosition(xFinal, yFinal, zFinal);

		PlanEquation eq = new PlanEquation(dp1, dp2, dp3);

		if (eq.equationSignum(pt1) > 0) {
			return pt1;
		}

		/*
		 * double dist1 = Math.sqrt((x1 - xFinal) * (x1 - xFinal) + (y1 -
		 * yFinal) (y1 - yFinal) + (z1 - zFinal) * (z1 - zFinal)); double dist2
		 * = Math.sqrt((x2 - xFinal) * (x2 - xFinal) + (y2 - yFinal) (y2 -
		 * yFinal) + (z2 - zFinal) * (z2 - zFinal)); double dist3 =
		 * Math.sqrt((x3 - xFinal) * (x3 - xFinal) + (y3 - yFinal) (y3 - yFinal)
		 * + (z3 - zFinal) * (z3 - zFinal)); System.out.println("Distance 1 " +
		 * dist1 + " Distance 2 " + dist2 + " Distance 3 " + dist3);
		 */

		return pt0;
	}

	public static IFeatureCollection<IFeature> processAlphaShape(IDirectPositionList dpl) {

		GM_Solid sol = AlphaShape.generateSol(dpl);

		TetraedrisationTopo t = new TetraedrisationTopo(new DefaultFeature(sol));

		t.tetraedriseWithNoConstraint(false);
		List<Tetraedre> lTet = t.getlTetraedres();

		FT_FeatureCollection<IFeature> ftFeat = new FT_FeatureCollection<IFeature>();

		int nbTetra = lTet.size();

		for (int i = 0; i < nbTetra; i++) {

			Tetraedre tet = lTet.get(i);

			// System.out.println(tet.getlNeighbour().size());

			List<Triangle> lTri = tet.getlTri();

			for (int j = 0; j < 4; j++) {

				Triangle tri = lTri.get(j);

				IDirectPosition dp = AlphaShape.determineSphereCenter(tri, AlphaShape.alpha);

				if (dp != null) {

					if (!AlphaShape.isNearerThan(dpl, dp, AlphaShape.alpha - 0.5)) {

						IFeature featTemp = new DefaultFeature(tri);

						ftFeat.add(featTemp);

					}

				}

			}
		}

		return ftFeat;
	}

	private static boolean isNearerThan(IDirectPositionList dpl, IDirectPosition dp, double distance) {
		int nbPoints = dpl.size();

		for (int i = 0; i < nbPoints; i++) {

			if (dp.distance(dpl.get(i)) < distance) {
				return true;
			}
		}
		return false;
	}
}
