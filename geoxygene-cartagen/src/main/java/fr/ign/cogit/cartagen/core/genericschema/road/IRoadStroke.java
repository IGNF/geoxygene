/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.core.genericschema.road;

import java.util.ArrayList;

import fr.ign.cogit.cartagen.core.genericschema.IGeneObjLin;
import fr.ign.cogit.cartagen.spatialanalysis.network.roads.RoadStroke;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.schemageo.api.support.reseau.ArcReseau;

/*
 * ###### IGN / CartAGen ###### Title: RoadStroke Description: Stroke routier
 * Author: J. Renard Date: 16/09/2009
 */

public interface IRoadStroke extends IGeneObjLin {

  public ILineString getGeomStroke();

  public ArrayList<ArcReseau> getFeatures();

  public RoadStroke getRoadStroke();

  public static final String FEAT_TYPE_NAME = "RoadStroke"; //$NON-NLS-1$
}
