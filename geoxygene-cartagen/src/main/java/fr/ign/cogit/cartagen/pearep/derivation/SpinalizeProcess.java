package fr.ign.cogit.cartagen.pearep.derivation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.genealgorithms.polygon.Spinalize;
import fr.ign.cogit.cartagen.mrdb.scalemaster.ProcessParameter;
import fr.ign.cogit.cartagen.mrdb.scalemaster.ScaleMasterGeneProcess;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;

public class SpinalizeProcess extends ScaleMasterGeneProcess {

  static Logger logger = Logger.getLogger(SpinalizeProcess.class.getName());

  // private Class<?> newClass;
  private boolean removeHoles;
  private double lengthMin, overSample;
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
    this.removeHoles = (Boolean) getParamValueFromName("remove_holes");
    this.lengthMin = (Double) getParamValueFromName("length_min");
    this.overSample = (Double) getParamValueFromName("over_sample");

  }

  @Override
  public void execute(IFeatureCollection<? extends IGeneObj> features)
      throws Exception {
    parameterise();

    List<IPolygon> listPoly = new ArrayList<IPolygon>();
    for (IGeneObj obj : features) {
      IPolygon poly = (IPolygon) obj.getGeom();
      listPoly.add(poly);
    }

    List<ILineString> listSkeleton = Spinalize.spinalize(listPoly, lengthMin,
        overSample, removeHoles);

    setListSkeletonFinal(listSkeleton);

  }

  @Override
  public String getProcessName() {
    return "skeletonize";
  }

  @Override
  public Set<ProcessParameter> getDefaultParameters() {
    Set<ProcessParameter> params = new HashSet<ProcessParameter>();
    // params.add(new ProcessParameter("linear_class", String.class, ""));
    params.add(new ProcessParameter("remove_holes", Boolean.class, true));
    params.add(new ProcessParameter("length_min", Double.class, 400.0));
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
