package fr.ign.cogit.geoxygene.sig3d.model.citygml.core;

import org.citygml4j.model.citygml.core.Site;

/**
 * 
 * @author MBrasebin
 * 
 */
public abstract class CG_AbstractSite extends CG_CityObject {

  public CG_AbstractSite(Site s) {
    super(s);

  }
  
  
  public CG_AbstractSite(){
    super();
  }

}
