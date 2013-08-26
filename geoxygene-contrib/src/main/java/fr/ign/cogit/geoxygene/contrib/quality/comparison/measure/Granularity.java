package fr.ign.cogit.geoxygene.contrib.quality.comparison.measure;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.contrib.geometrie.Distances;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;

/**
 * 
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * 
 * @copyright IGN
 * 
 * 
 * Compute the granularity of a linestring
 * 
 * @author JFGirres
 * 
 */
public class Granularity extends LineStringMeasure {
  public Granularity(ILineString lsRef, ILineString lsComp) {
    super(lsRef, lsComp);
  }

  @Override
  public void compute() {
    IDirectPositionList dplLsComp = new DirectPositionList();
    dplLsComp = this.getLsComp().coord();
    double granularityComp = Double.MAX_VALUE;
    for (int i = 1; i < dplLsComp.size(); i++) {
      IDirectPosition dp1 = dplLsComp.get(i);
      IDirectPosition dp2 = dplLsComp.get(i - 1);
      double distance = Distances.distance2D(dp1, dp2);
      if (granularityComp > distance) {
        granularityComp = distance;
      }
    }
    this.setMeasure(granularityComp);
  }
}
