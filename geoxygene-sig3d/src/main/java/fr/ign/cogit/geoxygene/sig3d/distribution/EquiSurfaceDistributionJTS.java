package fr.ign.cogit.geoxygene.sig3d.distribution;

import java.util.ArrayList;
import java.util.List;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ITriangle;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.geometrie.Vecteur;
import fr.ign.cogit.geoxygene.convert.FromGeomToSurface;
import fr.ign.cogit.geoxygene.sig3d.calculation.Util;
import fr.ign.cogit.geoxygene.sig3d.convert.geom.FromPolygonToTriangle;
import fr.ign.cogit.geoxygene.sig3d.tetraedrisation.Tetraedrisation;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Solid;
import fr.ign.cogit.geoxygene.util.conversion.AdapterFactory;

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
 * @version 1.6
 * 
 * @author MBrasebin
 * 
 *         Classe permettant de tirer aléatoirement des points répartis
 *         équitablement sur un objet de dimension 2 ou 3 Le principe est basé
 *         sur Shape Distributions Osada and al[2002] Shape Distributions ROBERT
 *         OSADA, THOMAS FUNKHOUSER, BERNARD CHAZELLE, and DAVID DOBKIN
 *         Princeton University Class wich calculate the dissimilarity measure
 *         between 2 solids
 * 
 * 
 *         Class to generate random points distrubition on an object with a
 *         dimension superior than 2. The principle is based on : Shape
 *         Distributions Osada and al[2002] Shape Distributions ROBERT OSADA,
 *         THOMAS FUNKHOUSER, BERNARD CHAZELLE, and DAVID DOBKIN Princeton
 *         University Class wich calculate the dissimilarity measure between 2
 *         solids
 * 
 */
public class EquiSurfaceDistributionJTS {

	private List<Geometry> lTriangles = new ArrayList<Geometry>();

	// Il s'agit des aires cumulés des différents triangles
	// airesCumulees[i] = somme des ieme premiers triangles
	// airesCumulees[n-1] = aire de la surface enblobant le corps
	private double[] accumulatedArea;

	private static GeometryFactory gf = new GeometryFactory();

	public EquiSurfaceDistributionJTS(IGeometry geom) throws Exception {

		List<IOrientableSurface> lTriangles = FromGeomToSurface
				.convertGeom(geom);

		lTriangles = initTri(lTriangles);
		initSamp(lTriangles);

		for (IOrientableSurface surf : lTriangles) {
			this.lTriangles.add(AdapterFactory.toGeometry(gf, surf));
		}

	}

	// Initialisation avant le calcul de la fonction de caractérisation
	private List<IOrientableSurface> initTri(List<IOrientableSurface> lTriangles) {

		boolean b = Util.containOnlyTriangleFaces(lTriangles);

		if (b) {

		} else {

			List<ITriangle> lTrianglesTemp = FromPolygonToTriangle
					.convertAndTriangle(lTriangles);

			if (lTrianglesTemp == null) {

				try {
					Tetraedrisation tet = new Tetraedrisation(new GM_Solid(
							lTriangles));

					tet.tetraedriseWithConstraint(true);

					lTriangles = tet.getTriangles();
				} catch (Exception e) {

					e.printStackTrace();
				}

			} else {
				lTriangles = new ArrayList<IOrientableSurface>();
				lTriangles.addAll(lTrianglesTemp);
			}

		}

		return lTriangles;
	}

	private void initSamp(List<IOrientableSurface> lTriangles) {

		int nbTriangles = lTriangles.size();

		// calcul des aires cumulées qui permettront de choisir les points
		// aléatoires
		accumulatedArea = new double[nbTriangles];

		for (int i = 0; i < lTriangles.size(); i++) {
			IDirectPositionList lDP = lTriangles.get(i).coord();

			Vecteur v1 = new Vecteur(lDP.get(0), lDP.get(1));
			Vecteur v2 = new Vecteur(lDP.get(0), lDP.get(2));

			Vecteur v3 = v1.prodVectoriel(v2);

			double aire = v3.norme() * 0.5;

			// Nous avons 2 points à tester, nous pouvons calculer la longeur
			if (i != 0) {
				this.accumulatedArea[i] = aire + this.accumulatedArea[i - 1];

				continue;
			}

			this.accumulatedArea[i] = aire;

		}
	}

	/**
	 * Fonction permettant de tirer aléatoirement un triangle sur une surface
	 * triangulée en prenant compte de l'aire
	 * 
	 * @return
	 */

	public Geometry randomTriangle(double rand) {

		// Tout d'abord on choisit aléatoirement un triangle
		// Sur la surface du volume (aléatoirement pondéré par l'aire des
		// triangles)
		int nbEleme = this.accumulatedArea.length - 1;
		double max = this.accumulatedArea[nbEleme];

		double alea = max * rand;

		int i = 0;
		// On choisit l'indice correspondant
		while (this.accumulatedArea[i] < alea) {
			i++;

		}

		double valArInf = i == 0 ? 0 : accumulatedArea[i - 1];

		corrected_rand = (alea - valArInf)
				/ (this.accumulatedArea[i] - valArInf);

		// On retourne la valeur correspondante à cet indice
		Geometry surf = this.lTriangles.get(i);

		return surf;

	}

	private double corrected_rand = -1;

	public double getCorrectedRand() {
		return corrected_rand;
	}

	/**
	 * Fonction permettant de retourner un point tiré aléatoirement sur la
	 * surface d'un triangle
	 * 
	 * @param triangle
	 * @return un point tiré aléatoirement sur le triangle
	 */
	private DirectPosition randomPointOnTriangles(Geometry triangle,
			double aleaX, double aleaY) {

		Coordinate[] coord = triangle.getCoordinates();

		Coordinate p1 = coord[0];
		Coordinate p2 = coord[1];
		Coordinate p3 = coord[2];

		// Cette méthode est conseillée dans l'article dont est issue la mesure
		double sqrtAleaX = Math.sqrt(aleaX);

		DirectPosition pointFinal = new DirectPosition(p1.x * (1 - sqrtAleaX)
				+ p2.x * sqrtAleaX * (1 - aleaY) + p3.x * aleaY * sqrtAleaX,

		p1.y * (1 - sqrtAleaX) + p2.y * sqrtAleaX * (1 - aleaY) + p3.y * aleaY
				* sqrtAleaX, p1.z * (1 - sqrtAleaX) + p2.z * sqrtAleaX
				* (1 - aleaY) + p3.z * aleaY * sqrtAleaX);

		return pointFinal;

	}

	public IDirectPosition sample() {
		return sample(Math.random(), Math.random());
	}

	public IDirectPosition sample(double rand1, double rand2) {
		Geometry sur = randomTriangle(rand1);
		return randomPointOnTriangles(sur, corrected_rand, rand2);
	}

	public IDirectPosition inversample(double x, double y) {

		

		for (Geometry t : lTriangles) {

			Coordinate[] coord = t.getCoordinates();
			IDirectPosition dp = getInverse(coord, x, y);

			if (dp.getX() <= 1 && dp.getX() >= 0 && dp.getY() <= 1
					&& dp.getY() >= 0) {
				return dp;
			}

		}

		/*
		 * 
		 * 
		 * 
		 * LineEquation l1 = new LineEquation(p1, p.getPosition()); LineEquation
		 * l2 = new LineEquation(p2, p3);
		 * 
		 * 
		 * 
		 * double det = p1.getX() * p2.getY() - p2.getX() * p1.getY();
		 * 
		 * 
		 * 
		 * 
		 * IDirectPosition dp = l1.intersectionLineLine(l2);
		 * 
		 * if (dp == null) { System.out.println("Erreur erreur"); return null; }
		 * 
		 * Vecteur ax = new Vecteur(p1, p.getPosition()); ax.normalise();
		 * Vecteur an = new Vecteur(p1, dp); an.normalise();
		 * 
		 * double prodS = ax.prodScalaire(an);
		 * 
		 * double eta1 = (1 - prodS) * (1 - prodS);
		 * 
		 * Vecteur bx = new Vecteur(p2, dp); bx.normalise(); Vecteur bc = new
		 * Vecteur(p2, p3); bc.normalise();
		 * 
		 * double prodS2 = bx.prodScalaire(bc);
		 * 
		 * double eta2 = 1 - prodS2;
		 */

		// double xtest = ( 1 - Math.sqrt(eta1)) * p1.getX() + Math.sqrt(eta1) *
		// (1
		// - eta2) * p2.getX() + Math.sqrt(eta1) * eta2 * p3.getX();

		// double ytest = ( 1 - Math.sqrt(eta1)) * p1.getY() + Math.sqrt(eta1) *
		// (1
		// - eta2) * p2.getY() + Math.sqrt(eta1) * eta2 * p3.getY();

		return null;

	}

	private DirectPosition getInverse(Coordinate[] coord, double x, double y) {

		Coordinate p1 = coord[0];
		Coordinate p2 = coord[1];
		Coordinate p3 = coord[2];

		double E0 = p2.x - p1.x;
		double E1 = p2.y - p1.y;

		double F0 = p3.x - p2.x;
		double F1 = p3.y - p2.y;

		double val2 = (E1 * (x - p1.x) - E0 * (y - p1.y)) / (F0 * E1 - F1 * E0);

		double val1 = (y - p1.y - val2 * F1) / E1;

		double eta1 = val1 * val1;

		double eta2 = val2 / val1;

		return new DirectPosition(eta1, eta2);

	}
}
