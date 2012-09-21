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
 * 
 * @version 0.1
 * 
 * Classe permettant de recoller les segments approximants au sol
 * 
 * 
 * Class for adjusting ground segments
 * 
 * 
 * 
 */
public class SegmentAdjustement {

  List<GM_LineString> lSSA3;
  double xa, ya;
  double x0b, y0b, x1b, y1b;
  double D0, D1;

  /**
   * Permet d'implémenter la classe et d'exécuter la fonction
   * 
   * @param lSSA2 les segments à recoller
   * @param z le z des semgents
   */
  public SegmentAdjustement(List<GM_LineString> lSSA2, double z) {

    this.lSSA3 = new ArrayList<GM_LineString>();
    this.lSSA3.add(lSSA2.get(0));

    for (int i = 1; i < lSSA2.size(); i++) {
      GM_LineString LSa = this.lSSA3.get(i - 1);
      this.xa = LSa.coord().get(1).getX();
      this.ya = LSa.coord().get(1).getY();

      GM_LineString LSb = lSSA2.get(i);

      this.x0b = LSb.coord().get(0).getX();
      this.y0b = LSb.coord().get(0).getY();
      this.x1b = LSb.coord().get(1).getX();
      this.y1b = LSb.coord().get(1).getY();

      this.D0 = (this.x0b - this.xa) * (this.x0b - this.xa)
          + (this.y0b - this.ya) * (this.y0b - this.ya);
      this.D1 = (this.x1b - this.xa) * (this.x1b - this.xa)
          + (this.y1b - this.ya) * (this.y1b - this.ya);

      DirectPositionList LC = new DirectPositionList();
      DirectPosition C0, C1;

      if (this.D0 < this.D1) {
        C0 = new DirectPosition(this.xa, this.ya, z);
        C1 = new DirectPosition(this.x1b, this.y1b, z);
      } else {
        C0 = new DirectPosition(this.xa, this.ya, z);
        C1 = new DirectPosition(this.x0b, this.y0b, z);
      }
      LC.add(C0);
      LC.add(C1);

      this.lSSA3.add(new GM_LineString(LC));
    }

    // on referme le cycle
    double xfinal0 = this.lSSA3.get(this.lSSA3.size() - 1).coord().get(0)
        .getX();
    double yfinal0 = this.lSSA3.get(this.lSSA3.size() - 1).coord().get(0)
        .getY();
    double xfinal1 = this.lSSA3.get(0).coord().get(0).getX();
    double yfinal1 = this.lSSA3.get(0).coord().get(0).getY();
    DirectPosition C0 = new DirectPosition(xfinal0, yfinal0, z);
    DirectPosition C1 = new DirectPosition(xfinal1, yfinal1, z);
    DirectPositionList LC = new DirectPositionList();

    LC.add(C0);

    LC.add(C1);

    GM_LineString LSfinal = new GM_LineString(LC);
    this.lSSA3.remove(this.lSSA3.size() - 1);
    this.lSSA3.add(LSfinal);
  }

  /**
   * 
   * @return les segments au sol recollés
   */
  public List<GM_LineString> getLSSadjusted() {
    return this.lSSA3;
  }

}
