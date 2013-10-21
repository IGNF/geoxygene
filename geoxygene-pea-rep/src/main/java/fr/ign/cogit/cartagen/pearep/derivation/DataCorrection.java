package fr.ign.cogit.cartagen.pearep.derivation;

import java.util.HashSet;
import java.util.Set;

import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.mrdb.scalemaster.ScaleMasterTheme;
import fr.ign.cogit.cartagen.pearep.enrichment.ScaleMasterPreProcess;
import fr.ign.cogit.cartagen.software.dataset.CartAGenDB;
import fr.ign.cogit.cartagen.software.dataset.CartAGenDocOld;
import fr.ign.cogit.cartagen.software.dataset.GeneObjImplementation;
import fr.ign.cogit.cartagen.software.dataset.SourceDLM;

public class DataCorrection {

  private ScaleMasterPreProcess process;
  private Set<ScaleMasterTheme> themes;
  private SourceDLM dbType;

  public DataCorrection(ScaleMasterPreProcess process,
      Set<ScaleMasterTheme> themes, SourceDLM dbType) {
    super();
    this.process = process;
    this.themes = themes;
    this.dbType = dbType;
  }

  public ScaleMasterPreProcess getProcess() {
    return process;
  }

  public void setProcess(ScaleMasterPreProcess process) {
    this.process = process;
  }

  public Set<ScaleMasterTheme> getThemes() {
    return themes;
  }

  public void setThemes(Set<ScaleMasterTheme> themes) {
    this.themes = themes;
  }

  /**
   * Trigger the data correction process of {@code this} on the classes
   * corresponding to {@code this} themes.
   * @param database
   * @throws Exception
   */
  @SuppressWarnings("unchecked")
  public void triggerDataCorrection() throws Exception {
    CartAGenDB database = CartAGenDocOld.getInstance().getDatabaseFromSource(
        this.dbType);
    Set<Class<? extends IGeneObj>> processedClasses = new HashSet<Class<? extends IGeneObj>>();
    for (ScaleMasterTheme theme : themes) {
      GeneObjImplementation impl = database.getGeneObjImpl();
      processedClasses.add((Class<? extends IGeneObj>) theme
          .getImplementationClasses(impl));
    }
    process.setProcessedClasses(processedClasses);
    process.execute(database);
  }

  public SourceDLM getDbType() {
    return dbType;
  }

  public void setDbType(SourceDLM dbType) {
    this.dbType = dbType;
  }
}
