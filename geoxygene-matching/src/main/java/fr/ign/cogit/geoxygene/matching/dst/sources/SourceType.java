package fr.ign.cogit.geoxygene.matching.dst.sources;

/**
 *
 *
 */
public interface SourceType {
  
  String CRITERE_GEOM = "CRITERE GEOMETRIQUE";
  String CRITERE_GEOM_EUCLI = "EuclidianDist";
  String CRITERE_GEOM_LINE_ORIENTAT = "LineOrientation";
  String CRITERE_GEOM_PARTIAL_FRECHET = "PartialFrechetDist";
  
  String CRITERE_TOPO = "CRITERE TOPONYMIQUE";
  String CRITERE_TOPO_LEVENSHTEIN = "LevenshteinDist";
  String CRITERE_TOPO_Hamming = "HammingDist";
  
  String CRITERE_SEM  = "CRITERE SEMANTIQUE";
  String CRITERE_SEM_WU_PALMER  = "WuPalmerDistance";

}
