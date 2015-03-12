package fr.ign.cogit.geoxygene.spatial.geomengine;

import fr.ign.cogit.geoxygene.api.spatial.AbstractGeomFactory;

public abstract class AbstractGeometryEngine {
  private static AbstractGeometryEngine instance;
  private static AbstractGeomFactory factory;
  private static String geometrie = "JTS";

  public static AbstractGeometryEngine getInstance() {
    return instance;
  }

  protected static void setInstance(AbstractGeometryEngine instance) {
    AbstractGeometryEngine.instance = instance;
  }

  public static String getGeometrie() {
    return AbstractGeometryEngine.geometrie;
  }

  public static void setGeometrie(String geometrie) {
    AbstractGeometryEngine.geometrie = geometrie;
  }

  public static AbstractGeomFactory getFactory() {
    return factory;
  }

  protected static void setFactory(AbstractGeomFactory factory) {
    AbstractGeometryEngine.factory = factory;
  };

  // to be overriden in children
  // protected static void init() {
  // }

}
