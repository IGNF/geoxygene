/*******************************************************************************
 * This software is released under the licence CeCILL
 *  
 *  see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 *  
 *  see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 *  
 *  @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.core.persistence;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;

public enum CollectionType {
  COLLECTION, SET, LIST, MAP, FEATURE_COLLECTION;

  public Class<?> getClassObject() {
    if (this.equals(SET))
      return Set.class;
    if (this.equals(LIST))
      return List.class;
    if (this.equals(MAP))
      return Map.class;
    if (this.equals(FEATURE_COLLECTION))
      return IFeatureCollection.class;
    return Collection.class;
  }
}
