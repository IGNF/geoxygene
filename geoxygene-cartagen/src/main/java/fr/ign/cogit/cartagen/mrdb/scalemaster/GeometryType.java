/*******************************************************************************
 * This software is released under the licence CeCILL
 *  
 *  see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 *  
 *  see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 *  
 *  @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.mrdb.scalemaster;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;

public enum GeometryType {
  POINT, LINE, POLYGON;

  public Class<? extends IGeometry> toGeomClass() {
    if (this.equals(POINT))
      return IPoint.class;
    if (this.equals(LINE))
      return ILineString.class;
    if (this.equals(POLYGON))
      return IPolygon.class;

    return null;
  }
}
