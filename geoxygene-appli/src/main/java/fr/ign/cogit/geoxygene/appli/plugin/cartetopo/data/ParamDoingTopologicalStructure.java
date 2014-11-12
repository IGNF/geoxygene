package fr.ign.cogit.geoxygene.appli.plugin.cartetopo.data;

import fr.ign.cogit.geoxygene.appli.plugin.matching.netmatcher.data.ParamFilenamePopulationEdgesNetwork;

public class ParamDoingTopologicalStructure {

  public boolean doRenduPlanaire = false;
  public boolean doFusionNoeudProche = false;
  public boolean doSuppNoeudIsole = false;
  public boolean doFiltreNoeudSimple = false;
  public boolean doFusionArcDouble = false;
  public boolean doCreationTopologieFace = false;

  public double tolerance;
  public double seuilFusion;

  public ParamFilenamePopulationEdgesNetwork paramDataset;

}
