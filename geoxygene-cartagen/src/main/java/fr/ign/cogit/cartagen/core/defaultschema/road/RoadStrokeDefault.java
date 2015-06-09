/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.core.defaultschema.road;

import java.util.ArrayList;

import fr.ign.cogit.cartagen.core.defaultschema.GeneObjLinDefault;
import fr.ign.cogit.cartagen.core.genericschema.road.IRoadStroke;
import fr.ign.cogit.cartagen.spatialanalysis.network.roads.RoadStroke;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.schemageo.api.support.reseau.ArcReseau;

public class RoadStrokeDefault extends GeneObjLinDefault implements IRoadStroke {

  private ILineString geomStroke;
  private RoadStroke stroke;

  public RoadStrokeDefault(ILineString geom, RoadStroke stroke) {
    super();
    this.setGeom(geom);
    this.geomStroke = geom;
    this.stroke = stroke;
  }

  @Override
  public ILineString getGeomStroke() {
    return geomStroke;
  }

  @Override
  public ArrayList<ArcReseau> getFeatures() {
    return stroke.getFeatures();
  }

  @Override
  public RoadStroke getRoadStroke() {
    return stroke;
  }

}
