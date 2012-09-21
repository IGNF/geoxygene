package fr.ign.cogit.geoxygene.sig3d.simplification.aglokada;

import java.util.ArrayList;
import java.util.List;

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
 * 
 * @version 0.1
 * 
 * 
 * @author Aurélien Velten Classe permettant de déterminer les différents cycles
 *         d'un batiment
 * 
 */
public class CyclesDetection {

  List<List<GM_LineString>> lCyclesByCut;

  // constructeur
  public CyclesDetection() {
    this.lCyclesByCut = new ArrayList<List<GM_LineString>>();
  }

  // méthode qui divise chaque coupe en Z en plusieurs cycles selon
  // l'adjacence
  public List<List<GM_LineString>> getListeCycles(List<GM_LineString> lLS) {

    boolean cont1 = true;
    boolean cont2 = true;
    int nb_P = 1;
    List<GM_LineString> coupe = lLS;
    List<GM_LineString> coupe_ok = new ArrayList<GM_LineString>();
    ArrayList<GM_LineString> coupe_temp = new ArrayList<GM_LineString>();
    List<GM_LineString> coupe_new = coupe;

    if (lLS.size() <= 3) {
      cont1 = false;
      this.lCyclesByCut.add(lLS);
    }

    while (cont1 == true) {

      for (int k = 0; k < coupe_new.size(); k++) {
        if (k == 0) {
          coupe_ok.add(coupe_new.get(k));
        } else {
          cont2 = true;
          GM_LineString LS1 = coupe_ok.get(coupe_ok.size() - 1);
          GM_LineString LS2 = coupe_new.get(k);
          double x = LS1.coord().get(nb_P).getX();
          double y = LS1.coord().get(nb_P).getY();
          double x0 = LS2.coord().get(0).getX();
          double y0 = LS2.coord().get(0).getY();
          double x1 = LS2.coord().get(1).getX();
          double y1 = LS2.coord().get(1).getY();

          if (x == x0 && y == y0) {
            coupe_ok.add(coupe_new.get(k));
            nb_P = 1;
            cont2 = false;
          }
          if (x == x1 && y == y1) {
            coupe_ok.add(coupe_new.get(k));
            nb_P = 0;
            cont2 = false;
          }
          if (cont2 == true) {
            coupe_temp.add(coupe_new.get(k));
          }
        }
      }

      this.lCyclesByCut.add(coupe_ok);

      if (coupe_temp.size() > 1) {
        coupe_ok = new ArrayList<GM_LineString>();
        coupe_new = coupe_temp;
        coupe_temp = new ArrayList<GM_LineString>();
      } else {
        cont1 = false;
      }
    }

    // on renvoie la liste des cycles (liste de liste de LineString)
    return this.lCyclesByCut;
  }
}
