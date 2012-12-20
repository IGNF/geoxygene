/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.mrdb;

import java.util.HashMap;
import java.util.Map;

import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.software.dataset.DigitalCartographicModel;
import fr.ign.cogit.cartagen.util.Interval;

/**
 * This class holds specific DCMs that are included in a MRDB system and thus
 * store scale range and point of view information for every geographic class of
 * the DB.
 * @author GTouya
 * 
 */
public class MrdbDcm extends DigitalCartographicModel {

  private Map<Class<? extends IGeneObj>, MrdbClassInfo> classInformation;

  public Interval<Integer> getClassScaleRange(Class<? extends IGeneObj> classObj) {
    return classInformation.get(classObj).getScaleRange();
  }

  public MRDBPointOfView getClassPointOfView(Class<? extends IGeneObj> classObj) {
    return classInformation.get(classObj).getPointOfView();
  }

  public void addMrdbInformation(Class<? extends IGeneObj> classObj,
      Interval<Integer> scaleRange, MRDBPointOfView pointOfView) {
    this.classInformation.put(classObj, new MrdbClassInfo(scaleRange,
        pointOfView));
  }

  public MrdbDcm() {
    super();
    this.classInformation = new HashMap<Class<? extends IGeneObj>, MrdbClassInfo>();
  }

  private class MrdbClassInfo {
    private Interval<Integer> scaleRange;
    private MRDBPointOfView pointOfView;

    Interval<Integer> getScaleRange() {
      return scaleRange;
    }

    MRDBPointOfView getPointOfView() {
      return pointOfView;
    }

    MrdbClassInfo(Interval<Integer> scaleRange, MRDBPointOfView pointOfView) {
      this.scaleRange = scaleRange;
      this.pointOfView = pointOfView;
    }
  }
}
