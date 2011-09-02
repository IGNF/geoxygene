/**
 * @author julien Gaffuri 26 juin 2009
 */
package fr.ign.cogit.geoxygene.shemageo.relief;

import fr.ign.cogit.geoxygene.api.schemageo.relief.ElementCaracteristiqueDuRelief;
import fr.ign.cogit.geoxygene.api.schemageo.support.champContinu.ChampContinu;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.shemageo.support.champContinu.ElementCaracteristiqueImpl;

/**
 * ligne orographique, oronyme, point cote, talus, etc.
 * 
 * @author julien Gaffuri 26 juin 2009
 * 
 */
public class ElementCaracteristiqueDuReliefImpl extends
    ElementCaracteristiqueImpl implements ElementCaracteristiqueDuRelief {

  public ElementCaracteristiqueDuReliefImpl(ChampContinu champContinu,
      IGeometry geom) {
    super(champContinu, geom);
  }

}
