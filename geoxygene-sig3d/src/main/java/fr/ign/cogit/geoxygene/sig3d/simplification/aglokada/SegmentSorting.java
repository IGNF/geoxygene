package fr.ign.cogit.geoxygene.sig3d.simplification.aglokada;

import java.util.ArrayList;
import java.util.List;

import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;

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
 * @author Aurélien Velten
 * 
 * @version 0.1
 * 
 * Classe permettant d'ordoner les segments par adjacence
 * 
 * Class to sort segments by related segments
 * 
 * 
 * 
 */
public class SegmentSorting {

  List<GM_LineString> lSSA2 = new ArrayList<GM_LineString>();
  double x0, y0, x1, y1, x2, y2;
  int nb_Snew, nb_Pnew, nb_S, nb_P;
  int[] LSStest;

  /**
   * Réordonne les segments au sol approximants par adjacence
   * 
   * @param lSSA1 les segments que l'on souhaite ordonner
   */
  public SegmentSorting(List<GM_LineString> lSSA1) {

    int nb = lSSA1.size() - 1;
    this.nb_Snew = 0;
    this.nb_Pnew = 1;
    this.LSStest = new int[lSSA1.size()];

    for (int i = 0; i < this.LSStest.length; i++) {
      this.LSStest[i] = 0;
    }
    this.LSStest[0] = 1;

    GM_LineString LSinitial = lSSA1.get(0);
    this.lSSA2.add(LSinitial);

    while (nb != 0) {
      GM_LineString LS1 = lSSA1.get(this.nb_Snew);
      this.x0 = LS1.coord().get(this.nb_Pnew).getX();
      this.y0 = LS1.coord().get(this.nb_Pnew).getY();

      // on cherche le point le plus près de (x0,y0) parmi les autres
      // faces
      this.getSandP(lSSA1, this.x0, this.y0);

      GM_LineString LS2 = lSSA1.get(this.nb_S);

      this.LSStest[this.nb_S] = 1;
      this.nb_Snew = this.nb_S;

      // Cas normal, le point suivant est le second point de l'arrète
      if (this.nb_P == 0) {
        this.nb_Pnew = 1;
        this.lSSA2.add(LS2);

      }
      // Cas inversé
      if (this.nb_P == 1) {

        DirectPositionList dpl = new DirectPositionList();
        dpl.add(LS2.coord().get(1));
        dpl.add(LS2.coord().get(0));

        this.lSSA2.add(new GM_LineString(dpl));

        this.nb_Pnew = 0;
      }
      nb--;
    }
  }

  /**
   * + renvoie les numéros du segment (de 0 à LSSA1.size()-1) et du point (0 ou
   * 1) le plus près du point de coordonnées (x0,y0)
   * 
   * @param LSSA1
   * @param xini
   * @param yini
   */

  private void getSandP(List<GM_LineString> LSSA1, double xini, double yini) {

    double x11, y11, x21, y21;
    double D, D1, D2, Dmin;
    double[] LD = new double[LSSA1.size()];

    // on cherche si un point est identique à (x0,y0)
    for (int i = 0; i < LSSA1.size(); i++) {
      if (this.LSStest[i] == 0) {
        GM_LineString LSa = LSSA1.get(i);
        x11 = LSa.coord().get(0).getX();
        y11 = LSa.coord().get(0).getY();
        x21 = LSa.coord().get(1).getX();
        y21 = LSa.coord().get(1).getY();

        if (x11 == xini && y11 == yini) {
          this.nb_S = i;
          this.nb_P = 0;
          return;
        }
        if (x21 == xini && y21 == yini) {
          this.nb_S = i;
          this.nb_P = 1;
          return;
        }
      }
    }

    // si aucun point n'est identique au point de coordonnées (x0,y0) alors
    // on cherche le point le plus près
    for (int i = 0; i < LSSA1.size(); i++) {
      if (this.LSStest[i] == 0) {
        GM_LineString LSb = LSSA1.get(i);
        x11 = LSb.coord().get(0).getX();
        y11 = LSb.coord().get(0).getY();
        x21 = LSb.coord().get(1).getX();
        y21 = LSb.coord().get(1).getY();
        D1 = (x11 - xini) * (x11 - xini) + (y11 - yini) * (y11 - yini);
        D2 = (x21 - xini) * (x21 - xini) + (y21 - yini) * (y21 - yini);
        D = Math.min(D1, D2);
        LD[i] = D;
      }
      if (this.LSStest[i] == 1) {
        LD[i] = -1;
      }
    }

    this.nb_S = 0;
    Dmin = 400000;
    for (int i = 0; i < LSSA1.size(); i++) {
      if (LD[i] < Dmin && LD[i] >= 0) {
        Dmin = LD[i];
        this.nb_S = i;
      }
    }

    GM_LineString LSc = LSSA1.get(this.nb_S);
    x11 = LSc.coord().get(0).getX();
    y11 = LSc.coord().get(0).getY();
    x21 = LSc.coord().get(1).getX();
    y21 = LSc.coord().get(1).getY();
    D1 = (x11 - xini) * (x11 - xini) + (y11 - yini) * (y11 - yini);
    D2 = (x21 - xini) * (x21 - xini) + (y21 - yini) * (y21 - yini);

    if (D1 < D2) {
      this.nb_P = 0;
      return;
    }
    this.nb_P = 1;
    return;
  }

  /**
   * 
   * @return les segments ordonnés
   */
  public List<GM_LineString> getLSSSorted() {
    return this.lSSA2;
  }

  /**
   * @return l'indice du dernier sommet
   */
  public int getNumberOfLastPoint() {
    return this.nb_Pnew;
  }

}
