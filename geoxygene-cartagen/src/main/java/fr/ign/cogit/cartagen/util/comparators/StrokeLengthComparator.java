/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.util.comparators;

import java.util.Comparator;

import fr.ign.cogit.cartagen.spatialanalysis.network.Stroke;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;

/**
 * A comparator to sort stroke features lists using the length of the stroke.
 * @author GTouya
 * 
 * @param <T>
 */
public class StrokeLengthComparator<T extends Stroke> implements Comparator<T> {

  public StrokeLengthComparator() {
    super();
  }

  @Override
  public int compare(T o1, T o2) {
    // get the geometries
    ILineString geom1 = o1.getGeomStroke();
    ILineString geom2 = o2.getGeomStroke();
    return new Long(Math.round(1000 * (geom1.length() - geom2.length())))
        .intValue();
  }

}
