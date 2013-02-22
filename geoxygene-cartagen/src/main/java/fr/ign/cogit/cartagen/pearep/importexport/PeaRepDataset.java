package fr.ign.cogit.cartagen.pearep.importexport;

import fr.ign.cogit.cartagen.core.genericschema.energy.IPipeLine;
import fr.ign.cogit.cartagen.software.CartAGenDataSet;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;

public class PeaRepDataset extends CartAGenDataSet {

  // ///////////////////////////////////////
  // STANDARD NAMES OF DATASET POPULATIONS
  // ///////////////////////////////////////

  public static final String PIPELINES_POP = "pipelines";

  @Override
  public String getPopNameFromObj(IFeature obj) {
    if (obj instanceof IPipeLine) {
      return PeaRepDataset.PIPELINES_POP;
    }
    return super.getPopNameFromObj(obj);
  }

  @Override
  public String getPopNameFromClass(Class<?> classObj) {
    if (IPipeLine.class.isAssignableFrom(classObj)) {
      return PeaRepDataset.PIPELINES_POP;
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

}
