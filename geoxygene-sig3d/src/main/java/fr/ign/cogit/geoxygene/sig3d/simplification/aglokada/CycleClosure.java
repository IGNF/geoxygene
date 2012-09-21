package fr.ign.cogit.geoxygene.sig3d.simplification.aglokada;

import java.util.ArrayList;
import java.util.List;

import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
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
 * @version 0.1
 * 
 * Classe permettant d'ajouter les segments manquants à un cycle Class used to
 * add missing segment to a cycle
 * 
 */
public class CycleClosure {

  List<GM_LineString> lSS2 = new ArrayList<GM_LineString>();

  /**
   * Ajoute le segment manquant à un cycle afin de le clore. Tous les sommets de
   * ce cycle se retrouvent à l'altitude z.
   * 
   * @param lSS1 La liste de segment que l'on veut clore pour former un cycle
   * @param z le z du cycle
   */
  public CycleClosure(List<GM_LineString> lSS1, double z) {

    double x, y, x0, y0, x1, y1;
    double D0, D1;
    int nb_P;
    boolean cont;
    // Coordinate[] LC = new Coordinate[2];
    nb_P = 1;

    for (int i = 0; i < lSS1.size() - 1; i++) {
      cont = true;
      GM_LineString LS = lSS1.get(i);
      x = LS.coord().get(nb_P).getX();
      y = LS.coord().get(nb_P).getY();
      this.lSS2.add(LS);

      GM_LineString LSa = lSS1.get(i + 1);
      x0 = LSa.coord().get(0).getX();
      y0 = LSa.coord().get(0).getY();
      x1 = LSa.coord().get(1).getX();
      y1 = LSa.coord().get(1).getY();

      if (x0 == x && y0 == y) {
        nb_P = 1;
        cont = false;
      }
      if (x1 == x && y1 == y) {
        nb_P = 0;
        cont = false;
      }

      if (cont == true) {
        D0 = (x0 - x) * (x0 - x) + (y0 - y) * (y0 - y);
        D1 = (x1 - x) * (x1 - x) + (y1 - y) * (y1 - y);

        if (D0 <= D1) {
          DirectPosition dp1 = new DirectPosition(x, y, z);
          DirectPosition dp2 = new DirectPosition(x0, y0, z);
          DirectPositionList dPL = new DirectPositionList();
          dPL.add(dp1);
          dPL.add(dp2);

          this.lSS2.add(new GM_LineString(dPL));
          nb_P = 1;
        } else {
          DirectPosition dp1 = new DirectPosition(x, y, z);
          DirectPosition dp2 = new DirectPosition(x1, y1, z);
          DirectPositionList dPL = new DirectPositionList();
          dPL.add(dp1);
          dPL.add(dp2);

          this.lSS2.add(new GM_LineString(dPL));
          nb_P = 0;
        }
      }
    }

    this.lSS2.add(lSS1.get(lSS1.size() - 1));

    // ajout de la liaison entre le dernier et le premier segment si
    // nécessaire
    GM_LineString LS_final = lSS1.get(lSS1.size() - 1);
    double x_final = LS_final.coord().get(nb_P).getX();
    double y_final = LS_final.coord().get(nb_P).getY();

    GM_LineString LS_initial = lSS1.get(0);
    double x_initial = LS_initial.coord().get(0).getX();
    double y_initial = LS_initial.coord().get(0).getY();

    if (x_final != x_initial || y_final != y_initial) {
      DirectPosition dp1 = new DirectPosition(x_final, y_final, z);
      DirectPosition dp2 = new DirectPosition(x_initial, y_initial, z);
      DirectPositionList dPL = new DirectPositionList();

      dPL.add(dp1);
      dPL.add(dp2);

      this.lSS2.add(new GM_LineString(dPL));
    }
  }

  /**
   * 
   * @return renvoie le cycle fermé
   */
  public List<GM_LineString> getLSSColsed() {
    return this.lSS2;
  }

}
