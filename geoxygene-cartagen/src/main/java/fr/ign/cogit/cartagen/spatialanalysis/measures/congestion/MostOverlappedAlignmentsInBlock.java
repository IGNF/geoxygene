/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
/**
 * 
 */
package fr.ign.cogit.cartagen.spatialanalysis.measures.congestion;

import java.util.ArrayList;
import java.util.List;

import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanAlignment;
import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanBlock;
import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanElement;

/**
 * @author JRenard 09/2011
 */
public class MostOverlappedAlignmentsInBlock {

  IUrbanBlock block;

  public MostOverlappedAlignmentsInBlock(IUrbanBlock block) {
    this.block = block;
  }

  /**
   * Computes the two most overlapped alignments of the block
   * @return a list of 2 elements: - the most overlapped alignment inside the
   *         block - and the alignment which is the most overlapping it
   */
  public List<IUrbanAlignment> compute() {

    List<IUrbanAlignment> overlappedAligns = new ArrayList<IUrbanAlignment>();
    double maxOverlappingRatio = 0.0;

    // Test of alignments one by one
    for (IUrbanAlignment align : this.block.getAlignments()) {
      double overlappingRatio = 0.0;
      double maxOverlappingArea = 0.0;
      IUrbanAlignment maxOverlappingAlignment = null;

      // Comparison to other alignments to compute overlapping
      for (IUrbanAlignment alignBis : this.block.getAlignments()) {
        if (alignBis.equals(align)) {
          continue;
        }
        // No overlapping
        if (!alignBis.getGeom().overlaps(align.getGeom())) {
          continue;
        }
        // Else, computation of overlapping
        double overlappingArea = alignBis.getGeom().intersection(
            align.getGeom()).area();
        overlappingRatio += overlappingArea / align.getGeom().area();
        // Test of overlapped area
        if (overlappingArea < maxOverlappingArea) {
          continue;
        }
        // Test if there are common buildings between the two alignments - if
        // not, alignment is taken into account
        boolean commonBuildings = false;
        for (IUrbanElement build : alignBis.getUrbanElements()) {
          if (align.getUrbanElements().contains(build)) {
            commonBuildings = true;
            break;
          }
        }
        if (commonBuildings) {
          continue;
        }
        // Taking into account the current compared alignment
        maxOverlappingAlignment = alignBis;
        maxOverlappingArea = overlappingArea;
      }

      if (overlappingRatio > maxOverlappingRatio
          && maxOverlappingAlignment != null) {
        overlappedAligns.clear();
        overlappedAligns.add(align);
        overlappedAligns.add(maxOverlappingAlignment);
        maxOverlappingRatio = overlappingRatio;
      }

    }

    return overlappedAligns;

  }

}
