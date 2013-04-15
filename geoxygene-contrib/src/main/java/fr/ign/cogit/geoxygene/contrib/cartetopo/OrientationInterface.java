package fr.ign.cogit.geoxygene.contrib.cartetopo;

/**
 * Orientation d'un réseau orienté.
 *
 * NB: ne pas confondre orientation définie par l'attribut "orientation" (traité ici),
 * et l'orientation définie implicitement par le sens de stockage de la géométrie
 * 
 */
public interface OrientationInterface {
  
  /** . */
  public final static int SENS_DIRECT = 1;

  public final static int SENS_INVERSE = -1;
  
  public final static int DOUBLE_SENS = 2;

}
