/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.core.defaultschema.urban;

import java.util.List;

import fr.ign.cogit.cartagen.core.defaultschema.GeneObjSurfDefault;
import fr.ign.cogit.cartagen.core.genericschema.urban.ISportsField;
import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanBlock;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.spatial.geomengine.GeometryEngine;
import fr.ign.cogit.geoxygene.util.algo.geomstructure.Segment;

public class SportsField extends GeneObjSurfDefault implements ISportsField {

  /**
   * The Block the urban element is part of.
   */
  private IUrbanBlock block;
  private SportsFieldType type = SportsFieldType.UNKNOWN;

  public void setType(SportsFieldType type) {
    this.type = type;
  }

  public SportsField(IPolygon geom, SportsFieldType type) {
    super();
    this.setGeom(geom);
    this.setInitialGeom(geom);
    this.setType(type);
  }

  @Override
  public IUrbanBlock getBlock() {
    return block;
  }

  @Override
  public void setBlock(IUrbanBlock block) {
    this.block = block;
  }

  @Override
  public SportsFieldType getType() {
    return type;
  }

  @Override
  public String getTypeSymbol() {
    return type.name();
  }

  @Override
  public ILineString getMedianGeom() {
    if (getGeom().coord().size() == 4) {
      // get the segments
      List<Segment> segments = Segment.getSegmentList(getGeom(), getGeom()
          .coord().get(0));
      Segment seg1 = segments.get(0);
      Segment seg2 = segments.get(2);
      if (segments.get(1).length() > seg1.length()) {
        seg1 = segments.get(1);
        seg2 = segments.get(3);
      }
      return GeometryEngine.getFactory().createLineSegment(
          seg1.getMiddlePoint(), seg2.getMiddlePoint());
    }
    return null;
  }

  @Override
  public IPolygon getSymbolGeom() {
    return getGeom();
  }

}
