/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.pearep.derivation;

import java.util.HashSet;
import java.util.Set;

import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.genealgorithms.polygon.RaposoSimplification;
import fr.ign.cogit.cartagen.mrdb.scalemaster.ProcessParameter;
import fr.ign.cogit.cartagen.mrdb.scalemaster.ScaleMasterGeneProcess;
import fr.ign.cogit.cartagen.software.dataset.CartAGenDB;
import fr.ign.cogit.cartagen.software.dataset.CartAGenDoc;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;

/**
 * Encapsulate the Douglas&Peucker algorithm to be used inside a ScaleMaster2.0
 * @author GTouya
 * 
 */
public class RaposoSimplifProcess extends ScaleMasterGeneProcess {

  private boolean method1, toblerRes;
  private Double initialScale;
  private static RaposoSimplifProcess instance = null;

  protected RaposoSimplifProcess() {
    // Exists only to defeat instantiation.
  }

  public static RaposoSimplifProcess getInstance() {
    if (instance == null) {
      instance = new RaposoSimplifProcess();
    }
    return instance;
  }

  @Override
  public void execute(IFeatureCollection<? extends IGeneObj> features) {
    parameterise();
    if (initialScale == null) {
      CartAGenDB db = CartAGenDoc.getInstance().getCurrentDataset()
          .getCartAGenDB();
      initialScale = db.getSourceDLM().getRelatedScale();
    }
    for (IGeneObj obj : features) {
      if (obj.isDeleted())
        continue;
      IGeometry geom = obj.getGeom();
      IGeometry newGeom = geom;
      RaposoSimplification algo = new RaposoSimplification(method1, toblerRes,
          initialScale);
      try {
        if (newGeom instanceof IPolygon)
          newGeom = algo.simplify((IPolygon) geom);
        else if (newGeom instanceof ILineString)
          newGeom = algo.simplify((ILineString) geom);
      } catch (Exception e) {
        // let initial geom if algorithm fails
      }
      obj.setGeom(newGeom);
    }
  }

  @Override
  public String getProcessName() {
    return "Raposo-Simplification";
  }

  @Override
  public void parameterise() {
    this.method1 = (Boolean) getParamValueFromName("use_method_1");
    this.toblerRes = (Boolean) getParamValueFromName("use_tobler_resolution");
  }

  @Override
  public Set<ProcessParameter> getDefaultParameters() {
    Set<ProcessParameter> params = new HashSet<ProcessParameter>();
    params.add(new ProcessParameter("use_method_1", Boolean.class, true));
    params.add(new ProcessParameter("use_tobler_resolution", Boolean.class,
        true));
    return params;
  }

}
