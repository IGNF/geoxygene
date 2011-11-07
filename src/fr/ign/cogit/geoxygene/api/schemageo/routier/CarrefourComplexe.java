/**
 * @author julien Gaffuri 26 juin 2009
 */
package fr.ign.cogit.geoxygene.api.schemageo.routier;

import java.util.HashSet;

import fr.ign.cogit.geoxygene.api.schemageo.support.reseau.AgregatReseau;

/**
 * carrefour complexe du reseau routier (rond-point, echangeur, etc.)
 * 
 * @author julien Gaffuri 26 juin 2009
 * 
 */
public interface CarrefourComplexe extends AgregatReseau {

  /**
   * @return le nom
   */
  public String getNom();

  public void setNom(String nom);

  
  /**
   * les tronçons de route situés à l'intérieur du carrefour complexe
   * @return
   */
  public HashSet<TronconDeRoute> getRoutesInternes();
  public void setRoutesInternes(HashSet<TronconDeRoute> routesInternes);
  
  /**
   * les tronçons de route qui connectent le carrefour complexe au reste du réseau
   * @return
   */
  public HashSet<TronconDeRoute> getRoutesExternes();
  public void setRoutesExternes(HashSet<TronconDeRoute> routesExternes);
}
