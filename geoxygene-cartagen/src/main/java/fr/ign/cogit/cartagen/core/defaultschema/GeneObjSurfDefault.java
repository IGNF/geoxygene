/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.core.defaultschema;

import fr.ign.cogit.cartagen.core.genericschema.IGeneObjSurf;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;

/**
 * This class extends the CartAGenGeoObjDefault class. It handles CartAGen
 * surfacic objects that have a (persistent) artifact in a Gothic database.
 * @author Cecile Duchene, IGN-F, COGIT Lab.
 */
public abstract class GeneObjSurfDefault extends GeneObjDefault implements
    IGeneObjSurf {

  @Override
  public IPolygon getGeom() {
    return (IPolygon) super.getGeom();
  }

}
