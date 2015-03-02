package fr.ign.cogit.cartagen.genealgorithms.relief;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.ign.cogit.cartagen.core.genericschema.relief.IContourLine;
import fr.ign.cogit.cartagen.software.dataset.CartAGenDoc;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;

/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
public class ContourLineSelection {

  private int selectionStep = 2;
  private boolean startZero = true;

  public ContourLineSelection(int selectionStep, boolean startZero) {
    super();
    this.selectionStep = selectionStep;
    this.startZero = startZero;
  }

  public int getSelectionStep() {
    return selectionStep;
  }

  public void setSelectionStep(int selectionStep) {
    this.selectionStep = selectionStep;
  }

  public boolean isStartZero() {
    return startZero;
  }

  public void setStartZero(boolean startZero) {
    this.startZero = startZero;
  }

  /**
   * Compute the contour line selection on all the contour lines of the dataset.
   */
  public void computeSelection() {
    IFeatureCollection<IContourLine> contours = CartAGenDoc.getInstance()
        .getCurrentDataset().getContourLines();
    if (startZero)
      executeZero(contours);
    else
      executeLowest(contours);
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
    int selectStep = altStep * selectionStep;

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
    int index = selectionStep;
    for (Double altitude : listAltitudes) {
      if (!(index == selectionStep)) {
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

}
