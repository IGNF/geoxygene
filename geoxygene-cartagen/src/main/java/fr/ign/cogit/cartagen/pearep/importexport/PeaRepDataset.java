package fr.ign.cogit.cartagen.pearep.importexport;

import fr.ign.cogit.cartagen.core.genericschema.energy.IPipeLine;
import fr.ign.cogit.cartagen.core.genericschema.urban.ISquareArea;
import fr.ign.cogit.cartagen.software.CartAGenDataSet;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;

public class PeaRepDataset extends CartAGenDataSet {

  // ///////////////////////////////////////
  // STANDARD NAMES OF DATASET POPULATIONS
  // ///////////////////////////////////////

  public static final String PIPELINES_POP = "pipelines";
  public static final String SQUARE_POP = "squares";

  @Override
  public String getPopNameFromObj(IFeature obj) {
    if (obj instanceof IPipeLine) {
      return PeaRepDataset.PIPELINES_POP;
    } else if (obj instanceof ISquareArea) {
      return PeaRepDataset.SQUARE_POP;
    }
    return super.getPopNameFromObj(obj);
  }

  @Override
  public String getPopNameFromClass(Class<?> classObj) {
    if (IPipeLine.class.isAssignableFrom(classObj)) {
      return PeaRepDataset.PIPELINES_POP;
    } else if (ISquareArea.class.isAssignableFrom(classObj)) {
      return PeaRepDataset.SQUARE_POP;
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
   * Gets the squares of the dataset
   * @return
   */
  @SuppressWarnings("unchecked")
  public IPopulation<ISquareArea> getSquareAreas() {
    return (IPopulation<ISquareArea>) this.getCartagenPop(
        PeaRepDataset.SQUARE_POP, ISquareArea.FEAT_TYPE_NAME);
  }

}
