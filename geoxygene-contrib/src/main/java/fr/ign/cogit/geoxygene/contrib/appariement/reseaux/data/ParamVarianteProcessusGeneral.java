package fr.ign.cogit.geoxygene.contrib.appariement.reseaux.data;

/**
 * 
 * 
 *
 */
public class ParamVarianteProcessusGeneral {
  
  private boolean forceAppariementSimple = false;
  private boolean redecoupageArcsNonApparies = false;
  private boolean redecoupageNoeudsNonApparies = false;
  
  private double varianteRedecoupageNoeudsNonApparies_DistanceNoeudArc = 100;
  private double varianteRedecoupageNoeudsNonApparies_DistanceProjectionNoeud = 50;
      
  private boolean varianteFiltrageImpassesParasites = false;
  private boolean varianteChercheRondsPoints = false;

}
