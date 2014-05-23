/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.pearep.derivation.processes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.core.genericschema.relief.IContourLine;
import fr.ign.cogit.cartagen.mrdb.scalemaster.ProcessParameter;
import fr.ign.cogit.cartagen.mrdb.scalemaster.ScaleMasterGeneProcess;
import fr.ign.cogit.cartagen.software.CartAGenDataSet;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;

/**
 * An original method to select contour lines for the ScaleMaster. The lowest
 * contour is preserved and a threshold t is used to preserve 1 contour every t
 * contours. (For instance, if the threshold is 5, the process preserve 1
 * contour every 5 contours).
 * @author JFGirres
 * 
 */
public class ContourSelectionProcess extends ScaleMasterGeneProcess {

  private int csThreshold;
  private boolean startZero = true;
  private static ContourSelectionProcess instance = null;

  public ContourSelectionProcess() {
    // Exists only to defeat instantiation.
  }

  public static ContourSelectionProcess getInstance() {
    if (instance == null) {
      instance = new ContourSelectionProcess();
    }
    return instance;
  }

  @SuppressWarnings("unchecked")
  @Override
  public void execute(IFeatureCollection<? extends IGeneObj> features,
      CartAGenDataSet currentDataset) {
    parameterise();
    IFeatureCollection<IContourLine> contourFeatures = (IFeatureCollection<IContourLine>) features;

    if (startZero)
      executeZero(contourFeatures);
    else
      executeLowest(contourFeatures);
  }

  @Override
  public String getProcessName() {
    return "Contour Selection";
  }

  /**
   * Select the contour lines every csThreshold lines, starting from the
   * altitude 0, even if the zero is not present in the dataset.
   * @param contourFeatures
   */
  private void executeZero(IFeatureCollection<IContourLine> contourFeatures) {
    // first find the altitude step
    List<Integer> altitudes = new ArrayList<Integer>();
    for (IContourLine contourLine : contourFeatures) {
      int altitude = new Double(contourLine.getAltitude()).intValue();
      if (altitudes.contains(altitude))
        continue;
      altitudes.add(altitude);
    }
    Collections.sort(altitudes);
    int altStep = 500;
    for (int i = 1; i < altitudes.size(); i++) {
      int diff = altitudes.get(i) - altitudes.get(i - 1);
      if (diff < altStep)
        altStep = diff;
    }

    // now compute the selection step from the step and the threshold
    int selectStep = altStep * csThreshold;

    // now select the contour lines with altitude proportionnal to selectStep
    for (IContourLine contourLine : contourFeatures) {
      if ((contourLine.getAltitude() % selectStep) != 0) {
        contourLine.eliminateBatch();
      }
    }
  }

  /**
   * Select the contour lines every csThreshold lines, starting from the lowest
   * altitude contour line (it corresponds to the VMAP2toVMAP1 rule).
   * @param contourFeatures
   */
  private void executeLowest(IFeatureCollection<IContourLine> contourFeatures) {

    // Map contour altitudes and sort them
    Map<Integer, Double> mapAltitudes = new HashMap<Integer, Double>();
    for (IContourLine contourLine : contourFeatures) {
      mapAltitudes.put(Integer.valueOf((int) (contourLine.getAltitude() / 10)),
          contourLine.getAltitude());
    }
    Collection<Double> altitudeValues = mapAltitudes.values();
    List<Double> listAltitudes = new ArrayList<Double>(altitudeValues);
    Collections.sort(listAltitudes);

    // Preserve the lowest contour, and one contour every a given threshold
    int index = csThreshold;
    for (Double altitude : listAltitudes) {
      if (!(index == csThreshold)) {
        for (IContourLine contourLine : contourFeatures) {
          if (contourLine.getAltitude() == altitude.doubleValue()) {
            contourLine.eliminateBatch();
          }
        }
      } else {
        index = 0;
      }
      index = index + 1;
    }
  }

  @Override
  public void parameterise() {
    this.csThreshold = (Integer) getParamValueFromName("contour_selection");
    if (this.hasParameter("start_zero"))
      this.startZero = (Boolean) getParamValueFromName("start_zero");
  }

  @Override
  public Set<ProcessParameter> getDefaultParameters() {
    Set<ProcessParameter> params = new HashSet<ProcessParameter>();
    params.add(new ProcessParameter("contour_selection", Integer.class, 2));
    params.add(new ProcessParameter("start_zero", Boolean.class, true));

    return params;
  }

}
