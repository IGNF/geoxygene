/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.carto.evaluation.clutter;

import java.util.Collection;

import fr.ign.cogit.cartagen.spatialanalysis.tessellations.gridtess.GridCell;
import fr.ign.cogit.cartagen.spatialanalysis.tessellations.gridtess.GridTessellation;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IEnvelope;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.style.Layer;
import fr.ign.cogit.geoxygene.style.StyledLayerDescriptor;

/**
 * A class that contains clutter measures related to map legibility researches
 * at Lund University (Lars Harrie, Hanna Stigmar).
 * 
 * @author Guillaume
 * 
 */
public class MapLegibilityMethod {

  private StyledLayerDescriptor sld;
  private IEnvelope window;
  private double scale = 25000.0;

  // thresholds for the Olsson threshold method. Values come from H. Stigmar
  // user tests.
  private int nbOfObjectsThreshold = 11;
  private boolean useNbOfObjects = true;
  private double lineLengthThreshold = 170.0;
  private boolean useLineLength = true;
  private double nbOfVerticesThreshold = 450;
  private boolean useNbOfVertices = true;
  private int nbOfObjTypesThreshold = 17;
  private boolean useNbOfObjTypes = true;
  private double overlapThreshold = 3.;
  private boolean useOverlap = false;
  private int angularityThreshold = 40;
  private boolean useAngularity = false;

  public MapLegibilityMethod(StyledLayerDescriptor sld, IEnvelope window) {
    this.sld = sld;
    this.window = window;
  }

  /**
   * Compute the threshold method for legibility measurement from (Olsson et al
   * 2011).
   * 
   * @param cellWidth
   * @return a grid with a true value when cells are legible and a false value
   *         when cells aren't legible
   */
  public GridTessellation<Boolean> getOlssonThresholdLegibility(double cellWidth) {
    // first build an empty tessellation
    GridTessellation<Boolean> grid = new GridTessellation<Boolean>(window,
        cellWidth, true);

    // compute the legibility in all cells
    for (GridCell<Boolean> cell : grid.getCells()) {
      IFeatureCollection<IFeature> cellObjects = new FT_FeatureCollection<>();
      int nbLayers = 0;
      // first, get the objects in the cell
      for (Layer layer : sld.getLayers()) {
        Collection<? extends IFeature> layerObjects = layer
            .getFeatureCollection().select(cell.getEnvelope());
        if (layerObjects == null)
          continue;
        if (layerObjects.size() == 0)
          continue;
        nbLayers++;
        cellObjects.addAll(layerObjects);
      }

      // first criterion, the number of objects
      if (useNbOfObjects) {
        // compute the nb of objects in the cell
        if (cellObjects.size() > nbOfObjectsThreshold) {
          System.out.println("number of objects");
          System.out.println(cellObjects.size());
          cell.setValue(false);
          continue;
        }
      }

      // second criterion, the total length of linear objects
      if (useLineLength) {
        // compute the total length of linear objects in the cell
        double lineLength = 0.0;
        for (IFeature feat : cellObjects)
          lineLength += feat.getGeom().length();
        // put the length in map cm, such as the threshold
        lineLength = lineLength / scale * 100.0;
        if (lineLength > lineLengthThreshold) {
          cell.setValue(false);
          System.out.println("line length");
          System.out.println(lineLength);
          continue;
        }
      }

      // third criterion, the total number of vertices in objects
      // geometries
      if (useNbOfVertices) {
        // compute the total number of vertices in objects geometries in
        // the cell
        int nbVertices = 0;
        for (IFeature feat : cellObjects)
          nbVertices += feat.getGeom().coord().size();
        if (nbVertices > nbOfVerticesThreshold) {
          cell.setValue(false);
          System.out.println("number of vertices");
          System.out.println(nbVertices);
          continue;
        }
      }

      // fourth criterion, the number of object types
      if (useNbOfObjTypes) {
        // compute the number of object typess in the cell
        if (nbLayers > nbOfObjTypesThreshold) {
          cell.setValue(false);
          System.out.println("number of object types");
          System.out.println(nbLayers);
          continue;
        }
      }

      // fifth criterion, the total angularity of linear objects
      if (useAngularity) {
        // compute the total angularity of linear objects in the cell.
        // Angularity is incremented each time a cell object geometry
        // changes direction.
        int angularity = 0;
        for (IFeature feat : cellObjects) {
          if (!(feat.getGeom() instanceof ILineString))
            continue;
          // TODO
        }
        if (angularity > angularityThreshold) {
          cell.setValue(false);
          System.out.println("angularity");
          System.out.println(angularity);
          continue;
        }
      }

      // last criterion, the total overlap between objects symbols
      if (useOverlap) {
        // compute the total overlap between objects symbols in the cell
        double overlap = 0.0;
        // TODO
        if (overlap > overlapThreshold) {
          cell.setValue(false);
          System.out.println("overlap");
          System.out.println(overlap);
          continue;
        }
      }
    }

    return grid;
  }

  public StyledLayerDescriptor getSld() {
    return sld;
  }

  public void setSld(StyledLayerDescriptor sld) {
    this.sld = sld;
  }

  public IEnvelope getWindow() {
    return window;
  }

  public void setWindow(IEnvelope window) {
    this.window = window;
  }

  public int getNbOfObjectsThreshold() {
    return nbOfObjectsThreshold;
  }

  public void setNbOfObjectsThreshold(int nbOfObjectsThreshold) {
    this.nbOfObjectsThreshold = nbOfObjectsThreshold;
  }

  public boolean isUseNbOfObjects() {
    return useNbOfObjects;
  }

  public void setUseNbOfObjects(boolean useNbOfObjects) {
    this.useNbOfObjects = useNbOfObjects;
  }

  public double getLineLengthThreshold() {
    return lineLengthThreshold;
  }

  public void setLineLengthThreshold(double lineLengthThreshold) {
    this.lineLengthThreshold = lineLengthThreshold;
  }

  public boolean isUseLineLength() {
    return useLineLength;
  }

  public void setUseLineLength(boolean useLineLength) {
    this.useLineLength = useLineLength;
  }

  public double getNbOfVerticesThreshold() {
    return nbOfVerticesThreshold;
  }

  public void setNbOfVerticesThreshold(double nbOfVerticesThreshold) {
    this.nbOfVerticesThreshold = nbOfVerticesThreshold;
  }

  public boolean isUseNbOfVertices() {
    return useNbOfVertices;
  }

  public void setUseNbOfVertices(boolean useNbOfVertices) {
    this.useNbOfVertices = useNbOfVertices;
  }

  public int getNbOfObjTypesThreshold() {
    return nbOfObjTypesThreshold;
  }

  public void setNbOfObjTypesThreshold(int nbOfObjTypesThreshold) {
    this.nbOfObjTypesThreshold = nbOfObjTypesThreshold;
  }

  public boolean isUseNbOfObjTypes() {
    return useNbOfObjTypes;
  }

  public void setUseNbOfObjTypes(boolean useNbOfObjTypes) {
    this.useNbOfObjTypes = useNbOfObjTypes;
  }

  public double getOverlapThreshold() {
    return overlapThreshold;
  }

  public void setOverlapThreshold(double overlapThreshold) {
    this.overlapThreshold = overlapThreshold;
  }

  public boolean isUseOverlap() {
    return useOverlap;
  }

  public void setUseOverlap(boolean useOverlap) {
    this.useOverlap = useOverlap;
  }

  public int getAngularityThreshold() {
    return angularityThreshold;
  }

  public void setAngularityThreshold(int angularityThreshold) {
    this.angularityThreshold = angularityThreshold;
  }

  public boolean isUseAngularity() {
    return useAngularity;
  }

  public void setUseAngularity(boolean useAngularity) {
    this.useAngularity = useAngularity;
  }

  public double getScale() {
    return scale;
  }

  public void setScale(double scale) {
    this.scale = scale;
  }

}
