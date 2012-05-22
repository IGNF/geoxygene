package fr.ign.cogit.geoxygene.example;

import java.util.Locale;

import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;

public class TestI18N {

  /**
   * @param args
   */
  public static void main(String[] args) {
    Locale.setDefault(Locale.ENGLISH);
    GM_LineString line1 = new GM_LineString(new DirectPositionList(
        new DirectPosition(0, 0), new DirectPosition(10, 0)));
    GM_LineString line2 = new GM_LineString(new DirectPositionList(
        new DirectPosition(0, 10), new DirectPosition(0, 20)));
    GM_Polygon polygon = new GM_Polygon(line2);
    line1.intersection(polygon);
    line1.union(polygon);
  }

}
