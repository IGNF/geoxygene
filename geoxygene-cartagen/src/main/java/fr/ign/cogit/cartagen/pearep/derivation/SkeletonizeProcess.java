package fr.ign.cogit.cartagen.pearep.derivation;

import java.lang.reflect.Constructor;
import java.util.HashSet;
import java.util.Set;

import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.genealgorithms.polygon.Skeletonize;
import fr.ign.cogit.cartagen.mrdb.scalemaster.ScaleMasterGeneProcess;
import fr.ign.cogit.cartagen.software.dataset.CartAGenDoc;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.generalisation.Filtering;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;

public class SkeletonizeProcess extends ScaleMasterGeneProcess {

  private Class<?> newClass;
  private boolean removeHoles;
  private double widthMin, sizeMin;
  private static SkeletonizeProcess instance = null;

  protected SkeletonizeProcess() {
    // Exists only to defeat instantiation.
  }

  public static SkeletonizeProcess getInstance() {
    if (instance == null) {
      instance = new SkeletonizeProcess();
    }
    return instance;
  }

  @Override
  public void parameterise() {
    try {
      this.newClass = Class
          .forName((String) getParamValueFromName("linear_class"));
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
    this.removeHoles = (Boolean) getParamValueFromName("remove_holes");
    this.widthMin = (Double) getParamValueFromName("width_min");
    this.sizeMin = (Double) getParamValueFromName("size_min");
  }

  @Override
  public void execute(IFeatureCollection<? extends IGeneObj> features)
      throws Exception {
    IPopulation<IGeneObj> pop = CartAGenDoc
        .getInstance()
        .getCurrentDataset()
        .getCartagenPop(
            CartAGenDoc.getInstance().getCurrentDataset()
                .getPopNameFromClass(newClass));
    for (IGeneObj obj : features) {
      if (obj.isDeleted())
        continue;
      if (!(obj.getGeom() instanceof IPolygon))
        continue;
      if (obj.getGeom().area() > this.sizeMin)
        continue;
      IPolygon geom = (IPolygon) Filtering.DouglasPeucker(
          (IPolygon) obj.getGeom(), 5.0);
      // the holes are removed from the polygonal geometry if necessary
      if (removeHoles)
        geom = new GM_Polygon(geom.getExterior());
      // get the linear features, already existing that touch the polygon
      Set<ILineString> network = new HashSet<ILineString>();
      for (IGeneObj objInter : pop.select(geom))
        network.add((ILineString) objInter.getGeom());
      // compute the straight skeleton
      Set<ILineString> skeleton = Skeletonize.connectSkeletonToNetwork(
          Skeletonize.skeletonizeStraightSkeleton(geom), network, geom);
      double length = 0.0;
      for (ILineString line : skeleton)
        length += line.length();
      double width = geom.area() / length;
      if (width > this.widthMin)
        continue;
      obj.eliminateBatch();
      for (ILineString newGeom : skeleton) {
        Constructor<?> constr = newClass.getConstructor(IGeometry.class);
        IGeneObj newObj = (IGeneObj) constr.newInstance(newGeom);

        pop.add(newObj);
      }
    }

  }

  @Override
  public String getProcessName() {
    return "skeletonize";
  }

}
