/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.core.defaultschema.land;

import fr.ign.cogit.cartagen.core.defaultschema.GeneObjSurfDefault;
import fr.ign.cogit.cartagen.core.genericschema.land.ISimpleLandUseArea;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.schemageo.api.occSol.ZoneOccSol;

/*
 * ###### IGN / CartAGen ###### Title: SimpleLandUseArea Description: Zones
 * d'occupation du sol simples Author: J. Renard Date: 18/09/2009
 */

public class SimpleLandUseArea extends GeneObjSurfDefault implements
    ISimpleLandUseArea {

  /**
   * The type of land use
   */
  private int type;
  private IPolygon geom;

  @Override
  public int getType() {
    return this.type;
  }

  @Override
  public void setType(int type) {
    this.type = type;
  }

  /**
   * Associated Geoxygene schema object
   */
  private ZoneOccSol geoxObj;

  /**
   * Constructor
   */
  public SimpleLandUseArea(ZoneOccSol geoxObj, int type) {
    super();
    this.geoxObj = geoxObj;
    this.setType(type);
    this.setInitialGeom(geoxObj.getGeom());
    this.setEliminated(false);
  }

  public SimpleLandUseArea(IPolygon poly, int type) {
    super();
    this.setType(type);
    this.setInitialGeom(poly);
    this.setEliminated(false);
    this.setGeom(poly);
  }

  @Override
  public IFeature getGeoxObj() {
    return this.geoxObj;
  }

  @Override
  public void setGeom(IGeometry geom) {
    this.geom = (IPolygon) geom;
  }

  @Override
  public IPolygon getGeom() {
    return geom;
  }

}
