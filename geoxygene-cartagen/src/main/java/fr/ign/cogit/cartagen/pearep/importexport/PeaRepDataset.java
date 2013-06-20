package fr.ign.cogit.cartagen.pearep.importexport;

import fr.ign.cogit.cartagen.core.genericschema.energy.IPipeLine;
import fr.ign.cogit.cartagen.core.genericschema.harbour.IBerthingLine;
import fr.ign.cogit.cartagen.core.genericschema.hydro.ICoastLine;
import fr.ign.cogit.cartagen.core.genericschema.hydro.IDitchLine;
import fr.ign.cogit.cartagen.core.genericschema.hydro.IInundationArea;
import fr.ign.cogit.cartagen.core.genericschema.hydro.IWaterPoint;
import fr.ign.cogit.cartagen.core.genericschema.land.IWoodLine;
import fr.ign.cogit.cartagen.core.genericschema.road.IBridgeLine;
import fr.ign.cogit.cartagen.core.genericschema.road.IBridgePoint;
import fr.ign.cogit.cartagen.core.genericschema.urban.IBuildLine;
import fr.ign.cogit.cartagen.core.genericschema.urban.ISquareArea;
import fr.ign.cogit.cartagen.software.CartAGenDataSet;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;

public class PeaRepDataset extends CartAGenDataSet {

  // ///////////////////////////////////////
  // STANDARD NAMES OF DATASET POPULATIONS
  // ///////////////////////////////////////

  public static final String PIPELINES_POP = "pipelines";
  public static final String WOODLINES_POP = "woodlines";
  public static final String SQUARE_POP = "squares";
  public static final String DITCH_LINE_POP = "ditchLines";
  public static final String INUNDATION_POP = "inundationAreas";
  public static final String BRIDGE_LINE_POP = "bridgeLines";
  public static final String BRIDGE_POINT_POP = "bridgePoints";
  public static final String COAST_LINE_POP = "coastLines";
  public static final String BUILD_LINE_POP = "buildLines";
  public static final String BERTHING_LINES = "berthingLines";

  @Override
  public String getPopNameFromObj(IFeature obj) {
    if (obj instanceof IPipeLine) {
      return PeaRepDataset.PIPELINES_POP;
    } else if (obj instanceof IWoodLine) {
      return PeaRepDataset.WOODLINES_POP;
    } else if (obj instanceof ISquareArea) {
      return PeaRepDataset.SQUARE_POP;
    } else if (obj instanceof IDitchLine) {
      return PeaRepDataset.DITCH_LINE_POP;
    } else if (obj instanceof IBridgeLine) {
      return PeaRepDataset.BRIDGE_LINE_POP;
    } else if (obj instanceof IBridgePoint) {
      return PeaRepDataset.BRIDGE_POINT_POP;
    } else if (obj instanceof ICoastLine) {
      return PeaRepDataset.COAST_LINE_POP;
    } else if (obj instanceof IBuildLine) {
      return PeaRepDataset.BUILD_LINE_POP;
    } else if (obj instanceof IBerthingLine) {
      return PeaRepDataset.BERTHING_LINES;
    } else if (obj instanceof IInundationArea) {
      return PeaRepDataset.INUNDATION_POP;
    }
    return super.getPopNameFromObj(obj);
  }

  @Override
  public String getPopNameFromClass(Class<?> classObj) {
    if (IPipeLine.class.isAssignableFrom(classObj)) {
      return PeaRepDataset.PIPELINES_POP;
    } else if (IWoodLine.class.isAssignableFrom(classObj)) {
      return PeaRepDataset.WOODLINES_POP;
    } else if (ISquareArea.class.isAssignableFrom(classObj)) {
      return PeaRepDataset.SQUARE_POP;
    } else if (IDitchLine.class.isAssignableFrom(classObj)) {
      return PeaRepDataset.DITCH_LINE_POP;
    } else if (IBridgeLine.class.isAssignableFrom(classObj)) {
      return PeaRepDataset.BRIDGE_LINE_POP;
    } else if (IBridgePoint.class.isAssignableFrom(classObj)) {
      return PeaRepDataset.BRIDGE_POINT_POP;
    } else if (ICoastLine.class.isAssignableFrom(classObj)) {
      return PeaRepDataset.COAST_LINE_POP;
    } else if (IBuildLine.class.isAssignableFrom(classObj)) {
      return PeaRepDataset.BUILD_LINE_POP;
    } else if (IBerthingLine.class.isAssignableFrom(classObj)) {
      return PeaRepDataset.BERTHING_LINES;
    } else if (IInundationArea.class.isAssignableFrom(classObj)) {
      return PeaRepDataset.INUNDATION_POP;
    }
    return super.getPopNameFromClass(classObj);
  }

  /**
   * Gets the pipelines of the dataset
   * @return
   */
  @SuppressWarnings("unchecked")
  public IPopulation<IPipeLine> getPipelines() {
    return (IPopulation<IPipeLine>) this.getCartagenPop(
        PeaRepDataset.PIPELINES_POP, IPipeLine.FEAT_TYPE_NAME);
  }

  /**
   * Gets the woodlines of the dataset
   * @return
   */
  @SuppressWarnings("unchecked")
  public IPopulation<IWoodLine> getWoodlines() {
    return (IPopulation<IWoodLine>) this.getCartagenPop(
        PeaRepDataset.WOODLINES_POP, IWoodLine.FEAT_TYPE_NAME);
  }

  /**
   * Gets the squares of the dataset
   * @return
   */
  @SuppressWarnings("unchecked")
  public IPopulation<ISquareArea> getSquareAreas() {
    return (IPopulation<ISquareArea>) this.getCartagenPop(
        PeaRepDataset.SQUARE_POP, ISquareArea.FEAT_TYPE_NAME);
  }

  /**
   * Gets the ditch lines of the dataset
   * @return
   */
  @SuppressWarnings("unchecked")
  public IPopulation<IDitchLine> getDitchLines() {
    return (IPopulation<IDitchLine>) this.getCartagenPop(
        PeaRepDataset.DITCH_LINE_POP, IDitchLine.FEAT_TYPE_NAME);
  }

  /**
   * Gets the bridge lines of the dataset
   * @return
   */
  @SuppressWarnings("unchecked")
  public IPopulation<IBridgeLine> getBridgeLines() {
    return (IPopulation<IBridgeLine>) this.getCartagenPop(
        PeaRepDataset.BRIDGE_LINE_POP, IBridgeLine.FEAT_TYPE_NAME);
  }

  /**
   * Gets the bridge lines of the dataset
   * @return
   */
  @SuppressWarnings("unchecked")
  public IPopulation<IBridgePoint> getBridgePoints() {
    return (IPopulation<IBridgePoint>) this.getCartagenPop(
        PeaRepDataset.BRIDGE_POINT_POP, IBridgePoint.FEAT_TYPE_NAME);
  }

  /**
   * Gets the coast lines of the dataset
   * @return
   */
  @SuppressWarnings("unchecked")
  public IPopulation<ICoastLine> getCoastLines() {
    return (IPopulation<ICoastLine>) this.getCartagenPop(
        PeaRepDataset.COAST_LINE_POP, ICoastLine.FEAT_TYPE_NAME);
  }

  /**
   * Gets the building lines of the dataset
   * @return
   */
  @SuppressWarnings("unchecked")
  public IPopulation<IBuildLine> getBuildLines() {
    return (IPopulation<IBuildLine>) this.getCartagenPop(
        PeaRepDataset.BUILD_LINE_POP, IBuildLine.FEAT_TYPE_NAME);
  }

  /**
   * Gets the berthing lines of the dataset
   * @return
   */
  @SuppressWarnings("unchecked")
  public IPopulation<IBerthingLine> getBerthingLines() {
    return (IPopulation<IBerthingLine>) this.getCartagenPop(
        PeaRepDataset.BERTHING_LINES, IBerthingLine.FEAT_TYPE_NAME);
  }

  /**
   * Gets the inundation areas of the dataset
   * @return
   */
  @SuppressWarnings("unchecked")
  public IPopulation<IInundationArea> getInundationAreas() {
    return (IPopulation<IInundationArea>) this.getCartagenPop(
        PeaRepDataset.INUNDATION_POP, IInundationArea.FEAT_TYPE_NAME);
  }

  /**
   * Gets the water points of the dataset
   * @return
   */
  @SuppressWarnings("unchecked")
  public IPopulation<IWaterPoint> getWaterPoints() {
    return (IPopulation<IWaterPoint>) this.getCartagenPop(
        PeaRepDataset.WATER_PT_POP, IWaterPoint.FEAT_TYPE_NAME);
  }

}
