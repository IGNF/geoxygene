package fr.ign.cogit.geoxygene.spatial.util;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.contrib.geometrie.Vecteur;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;

public class Resampler {
    /**
     * Méthode pour rééchantillonner une GM_LineString.
     * <p>
     * English: Resampling of a line.
     * @param ls a linestring
     * @param maxDistance maximum distance between 2 consecutive points
     * @return
     */
    public static ILineString resample(ILineString ls, double maxDistance) {
      IDirectPositionList list = ls.coord();
      return new GM_LineString(Resampler.resample(list, maxDistance), false);
    }
    /**
     * Méthode pour rééchantillonner une {@code IDirectPositionList}.
     * <p>
     * English: Resampling of a line.
     * @param list a IDirectPositionList
     * @param maxDistance maximum distance between 2 consecutive points
     * @return a resampled IDirectPositionList
     * @see IDirectPositionList
     */
    public static IDirectPositionList resample(IDirectPositionList list, double maxDistance) {
      IDirectPositionList resampledList = new DirectPositionList();
      IDirectPosition prevPoint = list.get(0);
      resampledList.add(prevPoint);
      for (int j = 1; j < list.size(); j++) {
        IDirectPosition nextPoint = list.get(j);
        double length = prevPoint.distance(nextPoint);
        Double fseg = new Double(length / maxDistance);
        int nseg = fseg.intValue();
        // make sure the distance between the resulting points is smaller than
        // maxDistance
        if (fseg.doubleValue() > nseg) {
          nseg++;
        }
        // compute the actual distance between the resampled points
        double d = length / nseg;
        if (nseg >= 1) {
          Vecteur v = new Vecteur(prevPoint, nextPoint).vectNorme();
          for (int i = 0; i < nseg - 1; i++) {
            IDirectPosition curPoint = new DirectPosition(prevPoint.getX() + (i + 1) * d
                * v.getX(), prevPoint.getY() + (i + 1) * d * v.getY(), prevPoint
                .getZ()
                + (i + 1) * d * v.getZ());
            resampledList.add(curPoint);
          }
        }
        resampledList.add(nextPoint);
        prevPoint = nextPoint;
      }
      return resampledList;
    }
}
