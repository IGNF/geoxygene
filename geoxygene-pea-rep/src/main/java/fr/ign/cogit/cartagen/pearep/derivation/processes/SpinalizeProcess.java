package fr.ign.cogit.cartagen.pearep.derivation.processes;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.genealgorithms.polygon.Spinalize;
import fr.ign.cogit.cartagen.mrdb.scalemaster.ProcessParameter;
import fr.ign.cogit.cartagen.mrdb.scalemaster.ScaleMasterGeneProcess;
import fr.ign.cogit.cartagen.mrdb.scalemaster.ScaleMasterTheme;
import fr.ign.cogit.cartagen.software.CartAGenDataSet;
import fr.ign.cogit.cartagen.software.dataset.CartAGenDB;
import fr.ign.cogit.cartagen.software.dataset.CartAGenDocOld;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineSegment;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.feature.Population;
import fr.ign.cogit.geoxygene.generalisation.Filtering;
import fr.ign.cogit.geoxygene.generalisation.GaussianFilter;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineSegment;

public class SpinalizeProcess extends ScaleMasterGeneProcess {

  static Logger logger = Logger.getLogger(SpinalizeProcess.class.getName());

  private Class<?> newClass;
  private boolean removeHoles;
  private double lengthMin, overSample;
  private double widthMin, sizeMin;
  private static SpinalizeProcess instance = null;

  private List<ILineString> listSkeletonFinal;

  public SpinalizeProcess() {
    // Exists only to defeat instantiation.
  }

  public static SpinalizeProcess getInstance() {
    if (instance == null) {
      instance = new SpinalizeProcess();
    }
    return instance;
  }

  @Override
  public void parameterise() {

    // try {
    // this.newClass = Class
    // .forName((String) getParamValueFromName("linear_class"));
    // } catch (ClassNotFoundException e) {
    // e.printStackTrace();
    // }

    String themeName = (String) getParamValueFromName("linear_theme");
    ScaleMasterTheme theme = this.getScaleMaster().getThemeFromName(themeName);
    CartAGenDB db = CartAGenDocOld.getInstance().getCurrentDataset()
        .getCartAGenDB();
    Set<Class<?>> classes = new HashSet<Class<?>>();
    classes.addAll(theme.getRelatedClasses());
    this.newClass = db.getGeneObjImpl().filterClasses(classes).iterator()
        .next();
    this.removeHoles = (Boolean) getParamValueFromName("remove_holes");
    this.lengthMin = (Double) getParamValueFromName("length_min");
    this.widthMin = (Double) getParamValueFromName("width_min");
    this.sizeMin = (Double) getParamValueFromName("size_min");
    this.overSample = (Double) getParamValueFromName("over_sample");

  }

  @Override
  public void execute(IFeatureCollection<? extends IGeneObj> features,
      CartAGenDataSet currentDataset) throws Exception {

    IPopulation<IGeneObj> pop = CartAGenDocOld
        .getInstance()
        .getCurrentDataset()
        .getCartagenPop(
            CartAGenDocOld.getInstance().getCurrentDataset()
                .getPopNameFromClass(newClass));

    IPopulation<IGeneObj> popValid = new Population<IGeneObj>();
    for (IGeneObj iGeneObj : pop) {
      if (!(iGeneObj.getGeom().coord().size() == 1)) {
        popValid.add(iGeneObj);
      }
    }

    // Filter multiSurfaces
    List<IPolygon> listPoly = new ArrayList<IPolygon>();
    for (IGeneObj obj : features) {
      if (obj.getGeom().isPolygon()) {
        IPolygon polygon = (IPolygon) obj.getGeom();
        boolean addpoly = true;
        for (IPolygon poly : listPoly) {
          if (polygon.coord().toString().equals(poly.coord().toString()))
            addpoly = false;
        }
        if (addpoly == true)
          listPoly.add(polygon);
      }
      if (obj.getGeom().isMultiSurface()) {
        IMultiSurface<IPolygon> multiPoly = (IMultiSurface<IPolygon>) obj
            .getGeom();
        for (IPolygon polygon : multiPoly.getList()) {
          boolean addpoly = true;
          for (IPolygon poly : listPoly) {
            if (polygon.coord().toString().equals(poly.coord().toString()))
              addpoly = false;
          }
          if (addpoly == true)
            listPoly.add(polygon);
        }
      }
    }

    // Compute the spinal column of the list of polygons
    List<ILineString> listSkeleton = Spinalize.spinalize(listPoly, lengthMin,
        overSample, removeHoles);

    // Update the geometry according to generalisation parameters
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
      Set<ILineString> network = new HashSet<ILineString>();
      IPolygon geom = (IPolygon) obj.getGeom();
      for (IGeneObj objInter : popValid.select(geom))
        network.add((ILineString) objInter.getGeom());

      List<ILineString> listSkeletonSelect = new ArrayList<ILineString>();
      for (ILineString ls : listSkeleton) {
        if (ls.within(geom)) {
          ILineString lsFilter = Filtering.DouglasPeuckerLineString(
              GaussianFilter.gaussianFilter(ls, 10, 1), 10);
          boolean addLs = true;
          for (ILineString lsSkeleton : listSkeletonSelect) {
            if (lsFilter.coord().toString()
                .equals(lsSkeleton.coord().toString()))
              addLs = false;
          }
          if (addLs == true) {
            listSkeletonSelect.add(lsFilter);
          }
        }
      }

      Set<ILineSegment> setSegment = new HashSet<ILineSegment>();
      for (ILineString ls : listSkeletonSelect) {
        for (int i = 0; i < ls.coord().size() - 1; i++) {
          IDirectPosition dp0 = ls.coord().get(i);
          IDirectPosition dp1 = ls.coord().get(i + 1);
          ILineSegment segment = new GM_LineSegment(dp0, dp1);
          setSegment.add(segment);
        }
      }

      // connect the skeleton to the network
      if (setSegment.size() == 0)
        continue;
      Set<ILineString> skeleton = Spinalize.connectSkeletonToNetwork(
          setSegment, network, geom);
      double length = 0.0;
      for (ILineString line : listSkeletonSelect)
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
    return "spinalize";
  }

  @Override
  public Set<ProcessParameter> getDefaultParameters() {
    Set<ProcessParameter> params = new HashSet<ProcessParameter>();
    params.add(new ProcessParameter("linear_theme", String.class, "waterl"));
    params.add(new ProcessParameter("remove_holes", Boolean.class, true));
    params.add(new ProcessParameter("length_min", Double.class, 400.0));
    params.add(new ProcessParameter("width_min", Double.class, 65.0));
    params.add(new ProcessParameter("over_sample", Double.class, 10.0));
    return params;
  }

  public List<ILineString> getListSkeletonFinal() {
    return listSkeletonFinal;
  }

  public void setListSkeletonFinal(List<ILineString> listSkeletonFinal) {
    this.listSkeletonFinal = listSkeletonFinal;
  }

}
