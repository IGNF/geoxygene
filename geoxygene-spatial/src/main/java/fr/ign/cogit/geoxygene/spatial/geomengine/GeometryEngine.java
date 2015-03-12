/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.geoxygene.spatial.geomengine;

import fr.ign.cogit.geoxygene.spatial.JTSGeomFactory;

public class GeometryEngine extends AbstractGeometryEngine {

  static {
    setInstance(new GeometryEngine());
  }

  private GeometryEngine() {
    switch (getGeometrie()) {
      case "JTS":
        setFactory(new JTSGeomFactory());
        break;
      case "SOCLE":
      default:
        System.out.println("not implemented -- reverting to JTS");
        setFactory(new JTSGeomFactory());
        break;
    }
  }

  // method to get the static block fired
  public static void init() {
    System.out.println(GeometryEngine.class.getName() + " initialized");
  }
}
