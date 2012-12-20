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

import fr.ign.cogit.cartagen.core.genericschema.IGeneObjLin;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;

/**
 * This class extends the CartAGenGeoObjDefault class. It handles CartAGen
 * linear objects that have a (persistent) artifact in a Gothic database.
 * @author Cecile Duchene, IGN-F, COGIT Lab.
 */
public abstract class GeneObjLinDefault extends GeneObjDefault implements
    IGeneObjLin {

  @Override
  public ILineString getGeom() {
    return (ILineString) super.getGeom();
  }

}
