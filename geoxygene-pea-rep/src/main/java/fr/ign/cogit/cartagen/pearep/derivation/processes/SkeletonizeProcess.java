package fr.ign.cogit.cartagen.pearep.derivation.processes;

import java.lang.reflect.Constructor;
import java.util.HashSet;
import java.util.Set;

import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.genealgorithms.polygon.Skeletonize;
import fr.ign.cogit.cartagen.mrdb.scalemaster.ProcessParameter;
import fr.ign.cogit.cartagen.mrdb.scalemaster.ScaleMasterGeneProcess;
import fr.ign.cogit.cartagen.mrdb.scalemaster.ScaleMasterTheme;
import fr.ign.cogit.cartagen.software.dataset.CartAGenDB;
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
    if (SkeletonizeProcess.instance == null) {
      SkeletonizeProcess.instance = new SkeletonizeProcess();
    }
    return SkeletonizeProcess.instance;
  }

  @Override
  public void parameterise() {
    String themeName = (String) this.getParamValueFromName("linear_theme");
    ScaleMasterTheme theme = this.getScaleMaster().getThemeFromName(themeName);
    CartAGenDB db = CartAGenDoc.getInstance().getCurrentDataset()
        .getCartAGenDB();
    Set<Class<?>> classes = new HashSet<Class<?>>();
    classes.addAll(theme.getRelatedClasses());
    this.newClass = db.getGeneObjImpl().filterClasses(classes).iterator()
        .next();
    this.removeHoles = (Boolean) this.getParamValueFromName("remove_holes");
    this.widthMin = (Double) this.getParamValueFromName("width_min");
    this.sizeMin = (Double) this.getParamValueFromName("size_min");
  }

  @Override
  public void execute(IFeatureCollection<? extends IGeneObj> features)
      throws Exception {
    IPopulation<IGeneObj> pop = CartAGenDoc
        .getInstance()
        .getCurrentDataset()
        .getCartagenPop(
            CartAGenDoc.getInstance().getCurrentDataset()
                .getPopNameFromClass(this.newClass));
    for (IGeneObj obj : features) {
      if (obj.isDeleted()) {
        continue;
      }
      if (!(obj.getGeom() instanceof IPolygon)) {
        continue;
      }
      if (obj.getGeom().area() > this.sizeMin) {
        continue;
      }
      IPolygon geom = (IPolygon) Filtering.DouglasPeucker(obj.getGeom(), 5.0);
      // the holes are removed from the polygonal geometry if necessary
      if (this.removeHoles) {
        geom = new GM_Polygon(geom.getExterior());
      }
      // get the linear features, already existing that touch the polygon
      Set<ILineString> network = new HashSet<ILineString>();
      if (pop != null) {
        for (IGeneObj objInter : pop.select(geom)) {
          network.add((ILineString) objInter.getGeom());
        }
      }
      // compute the straight skeleton
      Set<ILineString> skeleton = Skeletonize.connectSkeletonToNetwork(
          Skeletonize.skeletonizeStraightSkeleton(geom), network,
          (IPolygon) obj.getGeom());
      double length = 0.0;
      for (ILineString line : skeleton) {
        length += line.length();
      }
      double width = geom.area() / length;
      if (width > this.widthMin) {
        continue;
      }
      obj.eliminateBatch();
      for (ILineString newGeom : skeleton) {
        Constructor<?> constr = this.newClass.getConstructor(IGeometry.class);
        IGeneObj newObj = (IGeneObj) constr.newInstance(newGeom);
        pop.add(newObj);
      }
    }
  }

  @Override
  public String getProcessName() {
    return "skeletonize";
  }

  @Override
  public Set<ProcessParameter> getDefaultParameters() {
    Set<ProcessParameter> params = new HashSet<ProcessParameter>();
    params.add(new ProcessParameter("linear_theme", String.class, "waterl"));
    params.add(new ProcessParameter("remove_holes", Boolean.class, false));
    params.add(new ProcessParameter("width_min", Double.class, 20.0));
    params.add(new ProcessParameter("size_min", Double.class, 50000.0));
    return params;
  }

}
