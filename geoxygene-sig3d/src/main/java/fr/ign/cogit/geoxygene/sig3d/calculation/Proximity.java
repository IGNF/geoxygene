package fr.ign.cogit.geoxygene.sig3d.calculation;

import java.util.ArrayList;
import java.util.List;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;

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
 * @version 0.1
 * 
 *
 * 
 * Permet différents calculs de proximite Ces claculs fonctionnent sur le sommet
 * et non les objets (Point d'une surface le plus proche d'un point)
 * 
 * Class for proximity calcul, this class only use the point of objects
 * 
 * 
 * 
 */
public class Proximity {

	public IDirectPosition nearest;
	public IDirectPosition nearest2;
	public double distance;
	public IOrientableSurface containingFace;

	/**
	 * Initialise le calcul
	 */
	public Proximity() {

		this.nearest = null;
		this.nearest2 = null;
		this.distance = Double.NaN;
		this.containingFace = null;

	}

	/**
	 * méthode renvoyant : (ATTENTION : le calcul n'est- effectué que sur les
	 * points de la face) -la distance entre 1 liste de points et une liste de
	 * faces -le point de la liste de points le plus proche -la face la plus
	 * proche -Le point le plus proche de cette face
	 * 
	 * @param P1
	 * @param lF
	 */

	public void nearest(IDirectPosition P1, List<IOrientableSurface> lF) {

		int nb = lF.size();

		if (lF == null || nb == 0) {

			this.nearest = null;
			this.nearest2 = null;
			this.distance = Double.NaN;
			this.containingFace = null;
			
			return;
		}

		IOrientableSurface faceTemp = lF.get(0);

		IDirectPosition pTemp2 = faceTemp.coord().get(0);
		double dTemp = pTemp2.distance(P1);

		for (int i = 0; i < nb; i++) {
			IOrientableSurface fTemp = lF.get(i);
			IDirectPositionList lPTemp = fTemp.coord();
			int nbpoint = lPTemp.size();

			for (int j = 0; j < nbpoint; j++) {
				IDirectPosition p = lPTemp.get(j);
				double d = p.distance(P1);
				if (d < dTemp) {
					// On garde le point
					dTemp = d;
					pTemp2 = p;
					faceTemp = fTemp;
				}

			}

		}

		this.nearest = P1;
		this.nearest2 = pTemp2;
		this.distance = dTemp;
		this.containingFace = faceTemp;

	}

	/**
	 * méthode renvoyant : (ATTENTION : le calcul n'est- effectué que sur les
	 * points de la face) -la distance entre 1 liste de points et une liste de
	 * faces -le point de la liste de points le plus proche -la face la plus
	 * proche Le point le plus proche de cette face
	 * 
	 * @param lP1
	 * @param lF
	 */

	public void nearest(IDirectPositionList lP1, ArrayList<IOrientableSurface> lF) {


		int nb = lF.size(); 

		if ((nb == 0)||(lP1.size() == 0)) {  

			this.nearest = null;
			this.nearest2 = null;
			this.distance = Double.NaN;
			this.containingFace = null;
			
			return;
		}

		IOrientableSurface faceTemp = lF.get(0);

		this.nearest(lP1, faceTemp.coord());

		IDirectPosition pTemp1 = this.nearest;
		IDirectPosition pTemp2 = this.nearest2;
		double dTemp = this.distance;

		for (int i = 0; i < nb; i++) {

			this.nearest(lP1, lF.get(i).coord());

			if (this.distance < dTemp) {
				pTemp1 = this.nearest;
				pTemp2 = this.nearest2;
				dTemp = this.distance;
				faceTemp = this.containingFace;

			}

		}

		this.nearest = pTemp1;
		this.nearest2 = pTemp2;
		this.distance = dTemp;
		this.containingFace = faceTemp;

	}

	/**
	 * Renvoie le point de la liste LP1 le plus proche des points de la liste LP2
	 */
	public void nearest(IDirectPositionList lP1, IDirectPositionList lP2) {

		int nb = lP1.size();
		int nb2 = lP2.size(); 

		if ((nb == 0)||(nb2 == 0)) {

			this.nearest = null;
			this.distance = Double.NaN;
			return;

		}

		this.distance = Double.POSITIVE_INFINITY;

		for (int i = 0; i < nb; i++) {

			IDirectPosition dp1 = lP1.get(i);
			for (int j = 0; j < nb2; j++) {
				IDirectPosition dp2 = lP2.get(j);

				double d = dp1.distance(dp2);
				if (d < this.distance) {
					this.distance = d;
					this.nearest = dp1;
					this.nearest2 = dp2;
				}

			}

		}

	}

	/**
	 * Renvoie le point le plus pproche d'une liste de points par rapport à un
	 * autre point
	 * 
	 * @param dp
	 * @param lP1
	 */
	public IDirectPosition nearest(IDirectPosition dp, IDirectPositionList lP1) {
		int nb = lP1.size();

		if (nb == 0) {

			this.nearest = null;
			this.distance = Double.NaN;
			return null;

		}

		IDirectPosition pTemp = lP1.get(0);

		double d = dp.distance(pTemp);
		this.distance = d;

		for (int i = 1; i < nb; i++) {

			IDirectPosition p2 = lP1.get(i);

			double dtemp = dp.distance(p2);

			if (dtemp < d) {
				d = dtemp;
				pTemp = p2;

			}

		}

		this.nearest = pTemp;
		this.distance = d;

		return pTemp;

	}

}
