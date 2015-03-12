package fr.ign.cogit.geoxygene.sig3d.equation;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ITriangle;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.geometrie.Vecteur;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;

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
 * @author Poupeau
 * @author Bonin
 * 
 * @version 0.1
 * 
 *
 * 
 *  Classe permettant de définir des équations
 *         de plan de divers manières ainsi que de calculer la normale à partir
 *         des 3 premiers points du polygon
 */
public class PlanEquation {

	/*
	 * Les coefficients, l'équation et la normale au plan
	 */
	protected double coeffa;
	protected double coeffb;
	protected double coeffc;
	protected double coeffd;

	protected String equation;
	protected Vecteur normaleToPlane;

	public static double EPSILON = 0.001;

	@SuppressWarnings("unused")
	private final static Logger logger = Logger.getLogger(PlanEquation.class
			.getName());

	protected PlanEquation() {
		super();
	}

	/**
	 * Equation du plan à partir de 3 points
	 * 
	 * @param dpCenter
	 * @param dp1
	 * @param dp2
	 */
	public PlanEquation(IDirectPosition dpCenter, IDirectPosition dp1,
			IDirectPosition dp2) {

		// Création des vecteurs plans
		Vecteur v1 = new Vecteur(dpCenter, dp1);
		Vecteur v2 = new Vecteur(dpCenter, dp2);

		v1.normalise();
		v2.normalise();

		// On utilise la forme paramètrique : vect AM = k.vectU + t.vectV (k,t)
		// appart. à R2
		Vecteur normale = v1.prodVectoriel(v2);
		double a = normale.getX();
		double b = normale.getY();
		double c = normale.getZ();



		if (a == 0 && b == 0 && c == 0) {  

			return;
			//PlanEquation.logger.warn(Messages.getString("Plans.PointsAlignes"));

		}

		// Calcul de d
		double d = -a * dpCenter.getX() - b * dpCenter.getY() - c * dpCenter.getZ();

		this.coeffa = a;
		this.coeffb = b;
		this.coeffc = c;
		this.coeffd = d;
		this.normaleToPlane = new Vecteur(a, b, c);
		this.normaleToPlane.normalise();

		// Affichage de l'équation implicite de l'équation
		String equation_str = new String(a + "x +" + b + "y +" + c + "z +" + d
				+ " = 0");

		this.equation = equation_str;
	}

	/**
	 * Création d'un plan d'équation à partir des paramètres a, b, c, d
	 * @param a
	 * @param b
	 * @param c
	 * @param d
	 */
	public PlanEquation(double a, double b, double c, double d) {
		this.coeffa = a;
		this.coeffb = b;
		this.coeffc = c;
		this.coeffd = d;
		this.normaleToPlane = new Vecteur(a, b, c);
		this.normaleToPlane.normalise();

		String equation_str = new String(a + "x +" + b + "y +" + c + "z +" + d
				+ " = 0");

		this.equation = equation_str;
	}

	/**
	 * Equation de plan à partir d'un vecteur et d'un point
	 * 
	 * @param v1 vecteur normal au plan
	 * @param dp point contenu dans le plan
	 */
	public PlanEquation(Vecteur v1, IDirectPosition dp) {

		v1.normalise();
		double a = v1.getX();
		double b = v1.getY();
		double c = v1.getZ();

		// Calcul de d
		double d = -a * dp.getX() - b * dp.getY() - c * dp.getZ();

		this.coeffa = a;
		this.coeffb = b;
		this.coeffc = c;
		this.coeffd = d;

		// Affichage de l'équation implicite de l'équation
		String equation_str = new String(a + "x +" + b + "y +" + c + "z +" + d
				+ " = 0");

		this.equation = equation_str;
		this.normaleToPlane = new Vecteur(a, b, c);
		this.normaleToPlane.normalise();

	}

	/**
	 * Equation à partir d'une surface
	 * 
	 * @param face
	 */
	public PlanEquation(IOrientableSurface face) {

		this(face.coord());

	}

	/**
	 * Equation à partir d'une liste de points
	 * @param dpl
	 */
	public PlanEquation(IDirectPositionList lPoints) {
		// - 1 : récupèration de trois points pour définir son équation
		IDirectPosition pt1 = lPoints.get(0);
		IDirectPosition pt2 = lPoints.get(1);
		IDirectPosition pt3 = lPoints.get(2);

		// - 2 : Création de deux vecteurs du plan
		Vecteur v1 = new Vecteur(pt1, pt2);
		Vecteur v2 = new Vecteur(pt1, pt3);
		v1.normalise();
		v2.normalise();

		// - 3 : Vérification qu'ils ne sont pas alignés
		if (v1.prodScalaire(v2) == 0) {
			// Création du vecteur 1
			IDirectPosition dp1 = new DirectPosition();
			boolean bdp1 = true;
			if (pt1.equals(pt2, 0)) {
				for (int i = 0; i < lPoints.size(); i++) {
					dp1 = lPoints.get(i);
					if (bdp1) {
						if (!dp1.equals(pt1, 0) || !dp1.equals(pt2, 0)
								|| !dp1.equals(pt3, 0)) {
							v1 = new Vecteur(pt1, dp1);
							bdp1 = false;
						}
					}
				}// boucle i
			}

			// Création du vecteur 2
			IDirectPosition dp2 = new DirectPosition();

			if (pt1.equals(pt3, 0)) {
				for (int i = 0; i < lPoints.size(); i++) {
					dp2 = lPoints.get(i);
					if (!dp2.equals(pt1, 0) || !dp2.equals(pt2, 0) || !dp2.equals(pt3, 0)
							|| !dp2.equals(dp1, 0)) {
						v2 = new Vecteur(pt1, dp2);
					}
				}// boucle i
			}

		}

		// On utilise la forme paramètrique : vect AM = k.vectU + t.vectV (k,t)
		// appart. à R2
		Vecteur normale = v1.prodVectoriel(v2);
		double a = normale.getX();
		double b = normale.getY();
		double c = normale.getZ();

		// Calcul de d
		double d = -a * pt1.getX() - b * pt1.getY() - c * pt1.getZ();

		this.coeffa = a;
		this.coeffb = b;
		this.coeffc = c;
		this.coeffd = d;

		// Affichage de l'équation implicite de l'équation
		String equation_str = new String(a + "x +" + b + "y +" + c + "z +" + d
				+ " = 0");
		this.equation = equation_str;
		this.normaleToPlane = new Vecteur(a, b, c);
		this.normaleToPlane.normalise();

	}

	/**
	 * Equation à partir d'un plan permettant d'avoir dp dans la partie positive
	 * de l'équation
	 * 
	 * @param face la face permettant de calculer l'équation de plan
	 * @param dp point qui se trouvera dans la partie positive du plan généré
	 */
	public PlanEquation(IOrientableSurface face, IDirectPosition dp) {

		IDirectPositionList lPoints = face.coord();
		// - 1 : récupèration de trois points pour définir son équation
		IDirectPosition pt1 = lPoints.get(0);
		IDirectPosition pt2 = lPoints.get(1);
		IDirectPosition pt3 = lPoints.get(2);

		// - 2 : Création de deux vecteurs du plan
		Vecteur v1 = new Vecteur(pt1, pt2);
		Vecteur v2 = new Vecteur(pt1, pt3);

		if (Double.isNaN(v2.getCoord().getX())
				|| Double.isNaN(v2.getCoord().getY())
				|| Double.isNaN(v2.getCoord().getZ())) {
			v2 = new Vecteur(pt2, pt3);
		}

		v1.normalise();
		v2.normalise();

		// - 3 : Vérification qu'ils ne sont pas alignés
		if (v1.prodScalaire(v2) == 0) {
			// Création du vecteur 1
			IDirectPosition dp1 = new DirectPosition();
			boolean bdp1 = true;
			if (pt1.equals(pt2, 0)) {
				for (int i = 0; i < face.coord().size(); i++) {
					dp1 = face.coord().get(i);
					if (bdp1) {
						if (!dp1.equals(pt1, 0) || !dp1.equals(pt2, 0)
								|| !dp1.equals(pt3, 0)) {
							v1 = new Vecteur(pt1, dp1);
							bdp1 = false;
						}
					}
				}// boucle i
			}

			// Création du vecteur 2
			IDirectPosition dp2 = new DirectPosition();

			if (pt1.equals(pt3, 0)) {
				for (int i = 0; i < face.coord().size(); i++) {
					dp2 = face.coord().get(i);
					if (!dp2.equals(pt1, 0) || !dp2.equals(pt2, 0) || !dp2.equals(pt3, 0)
							|| !dp2.equals(dp1, 0)) {
						v2 = new Vecteur(pt1, dp2);
					}
				}// boucle i
			}

		}
		// On utilise la forme paramètrique : vect AM = k.vectU + t.vectV (k,t)
		// appart. à R2
		Vecteur normale = v1.prodVectoriel(v2);

		double a = normale.getX();
		double b = normale.getY();
		double c = normale.getZ();

		// Calcul de d
		double d = -a * pt1.getX() - b * pt1.getY() - c * pt1.getZ();

		this.coeffa = a;
		this.coeffb = b;
		this.coeffc = c;
		this.coeffd = d;

		// Affichage de l'équation implicite de l'équation
		String equation_str = new String(a + "x +" + b + "y +" + c + "z +" + d
				+ " = 0");

		this.equation = equation_str;

		// Orientation vers le centre de l'objet
		if (this.equationSignum(dp) < 0) {
			a = -a;
			b = -b;
			c = -c;
			d = -d;

			this.coeffa = a;
			this.coeffb = b;
			this.coeffc = c;
			this.coeffd = d;

			// Affichage de l'équation implicite de l'équation
			equation_str = new String(a + "x +" + b + "y +" + c + "z +" + d + " = 0");
			this.equation = equation_str;
		}

		this.normaleToPlane = new Vecteur(a, b, c);
		this.normaleToPlane.normalise();

	}

	/**
	 * Permet de calculer la valeur de l'équation en un point
	 * 
	 * @param dp point ou l'on calcule la valeur de l'équation
	 * @return la valeur de l'équation au point paramètre
	 */
	public double equationValue(IDirectPosition dp) {
		return (this.coeffa * dp.getX() + this.coeffb * dp.getY() + this.coeffc
				* dp.getZ() + this.coeffd);
	}

	/**
	 * Permet de calculer le signe de la valeur de l'équation en un point
	 * 
	 * @param dp point dont on calcule le signe de l'équation
	 * @return le signe de l'équation au point paramètre
	 */
	public double equationSignum(IDirectPosition dp) {
		return Math.signum(this.coeffa * dp.getX() + this.coeffb * dp.getY()
				+ this.coeffc * dp.getZ() + this.coeffd);
	}

	/**
	 * Permet de calculer l'intersection entre le plan et la droite passant par 2
	 * points
	 * 
	 * @param dp1
	 * @param dp2
	 * @return le point d'intersection entre le plan et la droite passant par les
	 *         2 points paramètres
	 */
	public IDirectPosition intersectionLinePlan(IDirectPosition dp1,
			IDirectPosition dp2) {
		// - 1 : Création d'un vecteur directeur de la droite
		Vecteur vectDirecteur = new Vecteur(dp1, dp2);
		vectDirecteur = vectDirecteur.vectNorme();

		double coeffa = this.coeffa;
		double coeffb = this.coeffb;
		double coeffc = this.coeffc;
		double coeffd = this.coeffd;

		if (Math.abs(vectDirecteur.getX()) < PlanEquation.EPSILON) {

			vectDirecteur.setX(0.0);
		}

		if (Math.abs(vectDirecteur.getY()) < PlanEquation.EPSILON) {

			vectDirecteur.setY(0.0);
		}

		if (Math.abs(vectDirecteur.getZ()) < PlanEquation.EPSILON) {

			vectDirecteur.setZ(0.0);
		}

		if (Math.abs(coeffa) < PlanEquation.EPSILON) {

			coeffa = 0;
		}

		if (Math.abs(coeffb) < PlanEquation.EPSILON) {

			coeffb = 0;
		}

		if (Math.abs(coeffc) < PlanEquation.EPSILON) {

			coeffc = 0;
		}

		if (Math.abs(coeffd) < PlanEquation.EPSILON) {

			coeffd = 0;
		}

		// - 2 : Calcul du paramètre k : Coefficient de la forme paramètrique de
		// l'équation de la droite
		double k = -(coeffa * dp1.getX() + coeffb * dp1.getY() + coeffc
				* dp1.getZ() + coeffd)
				/ (coeffa * vectDirecteur.getX() + coeffb * vectDirecteur.getY() + coeffc
						* vectDirecteur.getZ());

		if (Math.abs(k) < PlanEquation.EPSILON) {

			return null;
		}

		// - 3 : Calcul du point d'intersection
		DirectPosition dpIntersectionDroitePlan = (DirectPosition) dp1.clone();
		dpIntersectionDroitePlan.move(vectDirecteur.multConstante(k).getCoord());

		vectDirecteur.normalise();
		this.getNormale().normalise();

		return dpIntersectionDroitePlan;
	}

	/**
	 * Permet de calculer l'intersection entre le plan et la demi-droite passant
	 * par 2 points (le premier sommet servant de borne)
	 * 
	 * @param dp1
	 * @param dp2
	 * @return le point d'intersection entre la demie droite [dp1, dp2) et le plan
	 */
	public boolean intersecteHalfLinePlan(IDirectPosition dp1,
			IDirectPosition dp2, double dist) {

		IDirectPosition inter = this.intersectionLinePlan(dp1, dp2);
		Vecteur vectDirecteur = new Vecteur(dp1, dp2); 

		// -----------------------------------------------------------------------------
		// Testing parallel case
		Vecteur n = new Vecteur(coeffa,coeffb,coeffc);
		Vecteur v = new Vecteur(dp1,dp2);
		if (n.prodScalaire(v) == 0){

			// Testing if dp1 belongs to the plan
			if (equationValue(dp1) == 0){ 

				// Number of intersections = infinity
				return true;

			}
			else{

				// 0 intersection
				return false;

			}

		}
		// -----------------------------------------------------------------------------

		if (vectDirecteur.prodScalaire(new Vecteur(dp1, inter)) < 0

				|| (dp1.distance(inter) > dist) || Double.isNaN(inter.getX())

				|| Double.isInfinite(inter.getX())) {

			return false;

		}
		return true;
	}

	/**
	 * Permet de calculer l'intersection entre le plan et une droite définie par
	 * un point et un vecteur à une distance dist du sommet
	 * 
	 * @param dp1
	 * @param v1
	 * @param dist
	 * @return indique si l'intersection a lieu
	 */
	public boolean intersectionLinePlan(IDirectPosition dp1, Vecteur v1,
			double dist) {
		IDirectPosition dp2 = ((DirectPosition) dp1.clone());
		dp2.move(v1.getCoord());
		IDirectPosition inter = this.intersectionLinePlan(dp1, dp2);
		if ((dp1.distance(inter) > dist) || Double.isNaN(inter.getX())
				|| Double.isInfinite(inter.getX())) {
			return false;
		}
		return true;

	}

	/**
	 * Permet de calculer l'intersection entre le plan et une droite définie par
	 * un point et un vecteur
	 * 
	 * @param dp1
	 * @param v1
	 * @return le sommet de l'intersection
	 */
	public IDirectPosition intersectionLinePlan(IDirectPosition dp1, Vecteur v1) {
		IDirectPosition dp2 = ((DirectPosition) dp1.clone());
		dp2.move(v1.getCoord());
		return this.intersectionLinePlan(dp1, dp2);
	}

	/**
	 * Permet de calculer l'intersection entre le plan et une demi-droite définie
	 * par un point et un vecteur
	 * 
	 * @param dp1
	 * @param v1
	 * @return le sommet de l'intersection
	 */
	public IDirectPosition intersectionHalfLinePlan(IDirectPosition dp1,
			Vecteur v1) {

		// -----------------------------------------------------------------------------
		// Testing parallel case
		Vecteur n = new Vecteur(coeffa,coeffb,coeffc);

		if (n.prodScalaire(v1) == 0){

			// Testing if dp1 belongs to the plan
			if (equationValue(dp1) == 0){ 

				// Number of intersections = infinity
				return dp1;

			}
			else{

				// 0 intersection
				return null;

			}

		}
		// -----------------------------------------------------------------------------


		IDirectPosition dp2 = ((DirectPosition) dp1.clone());
		dp2.move(v1.getCoord());
		IDirectPosition inter = this.intersectionLinePlan(dp1, dp2);
		Vecteur vectDirecteur = new Vecteur(dp1, dp2);
		if (vectDirecteur.prodScalaire(new Vecteur(dp1, inter)) > 0) {
			return inter;
		}
		return null;

	}

	/**
	 * Renvoie grace à l'équation, l'altitude en dp
	 * 
	 * @param dp Point 2D
	 * @return l'altitude en dp
	 */
	public double getZ(IDirectPosition dp) {

		return -(this.coeffa * dp.getX() + this.coeffb * dp.getY() + this.coeffd)
				/ this.coeffc;
	}

	/**
	 * Permet de calculer l'intersection entre le plan et une demi-droite définie
	 * par un point et un vecteur a une distance dist
	 * 
	 * @param dp1
	 * @param v1
	 * @param dist
	 * @return indique si l'intersection a eu lieu
	 */
	public boolean intersectionHalfLinePlanDist(IDirectPosition dp1, Vecteur v1,
			double dist) {
		IDirectPosition dp2 = ((DirectPosition) dp1.clone());
		dp2.move(v1.getCoord());
		IDirectPosition inter = this.intersectionLinePlan(dp1, dp2);
		Vecteur vectDirecteur = new Vecteur(dp1, dp2);
		// vectDirecteur = vectDirecteur.vectNorme();
		if (vectDirecteur.prodScalaire(new Vecteur(dp1, inter)) < 0
				|| (dp1.distance(inter) > dist) || Double.isNaN(inter.getX())
				|| Double.isInfinite(inter.getX())) {
			return false;
		}
		return true;
	}

	/**
	 * Permet de calculer l'intersection entre le plan et une droite définie par
	 * un point et - le vecteur vecteur
	 * 
	 * @param dp1
	 * @param v1
	 * @param dist
	 * @return indique si l'intersection a eu lieu
	 */
	public boolean intersectionHalfLinePlan(DirectPosition dp1, Vecteur v1,
			double dist) {
		DirectPosition dp2 = ((DirectPosition) dp1.clone());
		dp2.move(v1.getCoord());
		IDirectPosition inter = this.intersectionLinePlan(dp1, dp2);
		Vecteur vectDirecteur = new Vecteur(dp1, dp2);
		// vectDirecteur = vectDirecteur.vectNorme();
		if (vectDirecteur.prodScalaire(new Vecteur(dp1, inter)) > 0
				|| (dp1.distance(inter) > dist) || Double.isNaN(inter.getX())
				|| Double.isInfinite(inter.getX())) {
			return false;
		}
		return true;
	}

	/**
	 * Calcule l'intersection entre ce plan et un triangle
	 * @param tri
	 */
	
	// TO FIX
	
	public IGeometry triangleIntersection(ITriangle tri) {

		IDirectPosition dp1 = tri.coord().get(0);
		IDirectPosition dp2 = tri.coord().get(1);
		IDirectPosition dp3 = tri.coord().get(2);

		LineEquation l1 = new LineEquation(dp1, dp2);
		LineEquation l2 = new LineEquation(dp2, dp3);
		LineEquation l3 = new LineEquation(dp3, dp1);

		IDirectPosition dpInter1 = l1.intersectionLinePlan(this);
		IDirectPosition dpInter2 = l2.intersectionLinePlan(this);
		IDirectPosition dpInter3 = l3.intersectionLinePlan(this);

		if (dpInter1 != null) {

			Vecteur vect = new Vecteur(dp1, dp2);
			Vecteur vectTemp = new Vecteur(dp1, dpInter1);



			double scal = vect.prodScalaire(vectTemp);

			if (scal < -EPSILON || vect.norme() < vectTemp.norme() + EPSILON) {
				// il n'est pas sur le triangle
				dpInter1 = null;

			}

		}

		if (dpInter2 != null) {

			Vecteur vect = new Vecteur(dp2, dp3);
			Vecteur vectTemp = new Vecteur(dp2, dpInter2);

			double scal = vect.prodScalaire(vectTemp);

			if (scal < -EPSILON || vect.norme() < vectTemp.norme() + EPSILON) {
				// il n'est pas sur le triangle
				dpInter2 = null;

			}

		}

		if (dpInter3 != null) {

			Vecteur vect = new Vecteur(dp3, dp1);
			Vecteur vectTemp = new Vecteur(dp3, dpInter3);

			double scal = vect.prodScalaire(vectTemp);

			if (scal < -EPSILON|| vect.norme() < vectTemp.norme() + EPSILON) {
				// il n'est pas sur le triangle
				dpInter3 = null;

			}

		}

		IDirectPositionList dpl = new DirectPositionList();



		if (dpInter1 != null) {
			dpl.add(dpInter1);
		}

		if (dpInter2 != null) {
			dpl.add(dpInter2);
		}

		if (dpInter3 != null) {
			dpl.add(dpInter3);
		}


		if (dpl.size() == 0) {
			return null;
		}

		if (dpl.size() == 1) {
			return new GM_Point(dpl.get(0));
		}

		if (dpl.size() == 2) {
			return new GM_LineString(dpl);
		}

		return tri;
	}

	/**
	 * Permet de calculer la valeur de l'équation de plan en un point
	 * 
	 * @param dp
	 * @return la valeur de l'équation au point dp
	 */
	public double positionPointInPlan(DirectPosition dp) {

		double result = (this.coeffa * dp.getX()) + (this.coeffb * dp.getY())
				+ (this.coeffc * dp.getZ()) + this.coeffd;
		return result;
	}

	public double getCoeffa() {
		return this.coeffa;
	}

	public double getCoeffb() {
		return this.coeffb;
	}

	public double getCoeffc() {
		return this.coeffc;
	}

	public void setCoeffc(double c) {
		this.coeffc = c;
	}

	public double getCoeffd() {
		return this.coeffd;
	}

	public String getEquation() {
		return this.equation;
	}

	/**
	 * @return Returns the normaleAuPlan.
	 */
	public Vecteur getNormale() {
		return this.normaleToPlane;
	}

	/**
	 * Renvoie le projeté orthonormal par rapport au plan
	 * 
	 * @param dp le point que l'on projeté sur le plan suivant la normale
	 * @return renvoie le sommet correponsdant au projeté
	 */
	public IDirectPosition normalCasting(IDirectPosition dp) {

		return this.intersectionLinePlan(dp, this.getNormale());

	}

}
