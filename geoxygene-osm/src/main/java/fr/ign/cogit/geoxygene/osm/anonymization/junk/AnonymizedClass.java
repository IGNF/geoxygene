package fr.ign.cogit.geoxygene.osm.anonymization.junk;


/**
 * Inutilisée pour l'instant, ne le sera peut etre jamais
 * classe considérée pour indiquer aux utilisateurs 
 * si les objets qu'ils manipulent sont passés par un 
 * processus d'anonymisation
 * 
 * Deprecated, certainly won't be used.
 * 
 * @author Matthieu Dufait
 */
@Deprecated
public interface AnonymizedClass {
  public boolean isAnonymized();
  public void setAnonymized(boolean b);
}
