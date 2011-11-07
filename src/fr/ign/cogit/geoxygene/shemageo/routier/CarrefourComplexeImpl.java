/**
 * @author julien Gaffuri 26 juin 2009
 */
package fr.ign.cogit.geoxygene.shemageo.routier;

import java.util.HashSet;

import fr.ign.cogit.geoxygene.api.schemageo.routier.CarrefourComplexe;
import fr.ign.cogit.geoxygene.api.schemageo.routier.TronconDeRoute;
import fr.ign.cogit.geoxygene.shemageo.support.reseau.AgregatReseauImpl;

/**
 * carrefour complexe du reseau routier (rond-point, echangeur, etc.)
 * 
 * @author julien Gaffuri 26 juin 2009
 * 
 */
public class CarrefourComplexeImpl extends AgregatReseauImpl implements
    CarrefourComplexe {

  /**
   * le nom
   */
  private String nom = ""; //$NON-NLS-1$
  private HashSet<TronconDeRoute> routesInternes;
  private HashSet<TronconDeRoute> routesExternes;

  @Override
  public String getNom() {
    return this.nom;
  }

  @Override
  public void setNom(String nom) {
    this.nom = nom;
  }

  @Override
  public HashSet<TronconDeRoute> getRoutesExternes() {
    return routesInternes;
  }

  @Override
  public HashSet<TronconDeRoute> getRoutesInternes() {
    return routesExternes;
  }

  @Override
  public void setRoutesExternes(HashSet<TronconDeRoute> routesExternes) {
    this.routesExternes = routesExternes;
  }

  @Override
  public void setRoutesInternes(HashSet<TronconDeRoute> routesInternes) {
    this.routesInternes = routesInternes;
  }

}
